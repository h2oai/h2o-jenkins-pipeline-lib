/**
 * s3up localArtifact:String remoteArtifact:String keepPrivate:Boolean = true
 */
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    sh """
    echo s3cmd --access_key=${ACCESS_KEY}\
          --secret_key=${SECRET_KEY}\
          --acl-private\
          put ${config.localArtifact} ${config.remoteArtifact} 
    """
}
