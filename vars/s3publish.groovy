#!/usr/bin/groovy

import com.cloudbees.groovy.cps.NonCPS

/**
 * Publish given private artifacts into public bucket.
 */
def call(body) {
    def config = [
        artifacts: [],
        _targetBucket: 'h2o-internal-release',
        credentialsId: "awsArtifactsUploader"
    ]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def destObjects = []
    withCredentials([[ $class: 'AmazonWebServicesCredentialsBinding', credentialsId: config.credentialsId]]) {
        for (String artifact in artifacts) {
            def destObject = artifact.replaceAll(/s3:\/\/[^\/]*/, "s3://${config._targetBucket}")
            sh """
            s3cmd --recursive --no-progress --acl-public $artifact $destObject
            """
            destObjects.add(destObject)
        }
    }

    return destObjects
}    

