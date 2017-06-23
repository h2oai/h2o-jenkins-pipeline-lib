#!/usr/bin/groovy

/**
 * s3up localArtifact:String remoteArtifact:String keepPrivate:Boolean = true
 */
def call(body) {
    def config = [keepPrivate:true, credentialsId:"awsArtifactsUploader"]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def aclPrivate = config.keepPrivate ? "--acl-private" : ""

    withCredentials([[ $class: 'AmazonWebServicesCredentialsBinding', credentialsId: config.credentialsId]]) {
        sh """
        echo "Uploading artifacts: ${config}"
        s3cmd --access_key=${AWS_ACCESS_KEY_ID} --secret_key=${AWS_SECRET_ACCESS_KEY} ${aclPrivate} put ${config.localArtifact} ${config.remoteArtifact} 
        """
    }
}
