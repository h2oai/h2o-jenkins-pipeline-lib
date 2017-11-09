#!/usr/bin/groovy

import static ai.h2o.ci.ColorUtils.*

/**
 * s3up 
 *  - groupId:String 
 *  - artifactId: String
 *  - majorVersion: String
 *  - buildVersion: String
 *  - localArtifact:String 
 *  - remoteArtifactBucket:String
 *  - keepPrivate:Boolean = true
 */
def call(body) {
    def config = [
        groupId              : "ai/h2o",
        remoteArtifactBucket : "s3://artifacts.h2o.ai/releases", 
        keepPrivate          : true, 
        credentialsId        : "awsArtifactsUploader",
        updateLatest         : true,
        isRelease            : true,]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // FIXME: check remoteArtifactBucket for suffix `/` and remove it

    def aclPrivate = config.keepPrivate ? "--acl-private" : ""
    if (!config.isRelease && config.remoteArtifactBucket == "s3://artifacts.h2o.ai/releases") {
        config.remoteArtifactBucket == "s3://artifacts.h2o.ai/snapshots"
    }

    withCredentials([[ $class: 'AmazonWebServicesCredentialsBinding', credentialsId: config.credentialsId]]) {
        sh """
        echo "Uploading artifacts: ${config}"
        s3cmd --recursive --no-progres --access_key=${AWS_ACCESS_KEY_ID} --secret_key=${AWS_SECRET_ACCESS_KEY} ${aclPrivate} put ${config.localArtifact} "${config.remoteArtifactBucket}/${config.groupId}/${config.artifactId}/${config.majorVersion}.${config.buildVersion}/"
        """
        echo green("S3UP: ${config.localArtifact} --> ${config.remoteArtifactBucket}/${config.groupId}/${config.artifactId}/${config.majorVersion}.${config.buildVersion}/")

        if (config.updateLatest) {
            sh """
            s3cmd --recursive --no-progress --access_key=${AWS_ACCESS_KEY_ID} --secret_key=${AWS_SECRET_ACCESS_KEY} ${aclPrivate} put ${config.localArtifact} "${config.remoteArtifactBucket}/${config.groupId}/${config.artifactId}/${config.majorVersion}.latest/"
            """
        }
    }
}
