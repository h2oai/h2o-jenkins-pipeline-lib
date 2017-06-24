#!/usr/bin/groovy

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
        groupId: "ai/h2o",
        remoteArtifactBucket:"s3://artifacts.h2o.ai", 
        keepPrivate:true, 
        credentialsId:"awsArtifactsUploader"]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def aclPrivate = config.keepPrivate ? "--acl-private" : ""

    withCredentials([[ $class: 'AmazonWebServicesCredentialsBinding', credentialsId: config.credentialsId]]) {
        sh """
        echo "Uploading artifacts: ${config}"
        s3cmd --access_key=${AWS_ACCESS_KEY_ID} --secret_key=${AWS_SECRET_ACCESS_KEY} ${aclPrivate} put ${config.localArtifact} "${config.remoteArtifactBucket}/${config.groupId}/${config.artifactId}/${config.majorVersion}/${config.buildVersion}"
        s3cmd --access_key=${AWS_ACCESS_KEY_ID} --secret_key=${AWS_SECRET_ACCESS_KEY} ${aclPrivate} put ${config.localArtifact} "${config.remoteArtifactBucket}/${config.groupId}/${config.artifactId}/${config.majorVersion}/latest"
        """
    }
}
