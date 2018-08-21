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
    final def UPLOAD_ENGINE_DEFAULT = 'default'
    final def UPLOAD_ENGINE_AWSCLI = 'awscli'
    final def UPLOAD_ENGINE_S3CMD = 's3cmd'
    def config = [
        groupId              : "ai/h2o",
        remoteArtifactBucket : "s3://artifacts.h2o.ai/releases",
        keepPrivate          : true,
        credentialsId        : "awsArtifactsUploader",
        isRelease            : true,
        platform             : null,
        uploadEngine         : UPLOAD_ENGINE_DEFAULT
    ]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

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

    def awscliAvailable = sh(script: 'which aws', returnStatus: true) == 0

    final def useAwscli = config.uploadEngine == UPLOAD_ENGINE_AWSCLI
    if (useAwscli && !awscliAvailable) {
        error 'AWS CLI not available!'
    }

    final def useS3cmd = config.uploadEngine == UPLOAD_ENGINE_S3CMD || config.uploadEngine == UPLOAD_ENGINE_DEFAULT
    if (config.uploadEngine == UPLOAD_ENGINE_DEFAULT && useS3cmd) {
        echo yellow("WARNING! Using s3cmd, please upgrade shared lib to test-shared-library@1.7 or install awscli, so faster awscli can be used.")
    }

    withCredentials([[ $class: 'AmazonWebServicesCredentialsBinding', credentialsId: config.credentialsId]]) {
        // if available or explicitly specified
        if (useAwscli) {
            def aclFlag = config.keepPrivate ? "private" : "public-read"
            sh "aws s3 sync ${config.localArtifact} ${targetObject} --no-progres --acl ${aclFlag}"
        } else if (useS3cmd) {
            def aclFlag = config.keepPrivate ? "--acl-private" : "--acl-public"
            sh "s3cmd --recursive --no-progres --access_key=${AWS_ACCESS_KEY_ID} --secret_key=${AWS_SECRET_ACCESS_KEY} ${aclFlag} put ${config.localArtifact} ${targetObject}"
        } else {
            error "Cannot use awscli nor s3cmd!"
        }
        echo green("S3UP: ${config.localArtifact} --> ${targetObject}")

    }
    return targetObject
}
