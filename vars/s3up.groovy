#!/usr/bin/groovy

/**
 * s3up localArtifact:String remoteArtifact:String keepPrivate:Boolean = true
 */
def call(body) {
    def config = [keepPrivate:true]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def aclPrivate = config.keepPrivate ? "--acl-private" : ""

    sh """
    echo "Uploading artifacts: ${config}"
    s3cmd --access_key=${env.ACCESS_KEY} --secret_key=${env.SECRET_KEY} ${aclPrivate} put ${config.localArtifact} ${config.remoteArtifact} 
    """
}
