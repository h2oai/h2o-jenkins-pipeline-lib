#!/usr/bin/groovy

import static ai.h2o.ci.ColorUtils.*

/**
 * s3up
 *  - groupId: String
 *  - artifactId: String
 *  - version: String
 *  - majorVersion: String
 *  - buildVersion: String
 *  - platform: String
 *  - localArtifact: String
 *  - remoteArtifactBucket: String
 *  - keepPrivate:Boolean = true
 */
def call(body) {
    def config = [
        groupId              : "ai/h2o",
        remoteArtifactBucket : "s3://artifacts.h2o.ai/releases",
        keepPrivate          : true,
        credentialsId        : "awsArtifactsUploader",
        isRelease            : true,
        platform: null
    ]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    echo "${config}"

    // FIXME: check remoteArtifactBucket for suffix `/` and remove it

    def aclPrivate = config.keepPrivate ? "--acl-private" : "--acl-public"
    if (!config.isRelease && config.remoteArtifactBucket == "s3://artifacts.h2o.ai/releases") {
        config.remoteArtifactBucket = "s3://artifacts.h2o.ai/snapshots"
    }

    def artifactVersion = config.containsKey('version') ? config.version : "${config.majorVersion}.${config.buildVersion}"

    def targetObject = "${config.remoteArtifactBucket}/${config.groupId}/${config.artifactId}/${artifactVersion}/"
    if (config.platform != null && config.platform != '') {
        targetObject += config.platform + '/'
    }
    withCredentials([[ $class: 'AmazonWebServicesCredentialsBinding', credentialsId: config.credentialsId]]) {
        def awscliAvailable = sh(script: 'which aws', returnCode: true) == 0
        echo "Uploading artifacts: ${config}"
        if (awscliAvailable) {
            def aclFlag = config.keepPrivate ? "private" : "public-read"
            sh "aws s3 cp ${config.localArtifact} ${targetObject} --recursive --no-progres --acl ${aclFlag}"
        } else {
            echo yellow("WARNING! Using s3cmd, please upgrade shared lib to test-shared-library@1.7 or install awscli, so faster awscli can be used.")
            def aclPrivate = config.keepPrivate ? "--acl-private" : "--acl-public"
            sh "s3cmd --recursive --no-progres --access_key=${AWS_ACCESS_KEY_ID} --secret_key=${AWS_SECRET_ACCESS_KEY} ${aclPrivate} put ${config.localArtifact} ${targetObject}"
        }
        echo green("S3UP: ${config.localArtifact} --> ${targetObject}")

    }
    return targetObject
}
