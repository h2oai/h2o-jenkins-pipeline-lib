#!/usr/bin/groovy

/**
 * s3up localArtifact:String remoteArtifact:String keepPrivate:Boolean = true
 */
def call(body) {
    def config = [keepPrivate:true]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    //echo s3cmd --access_key=${env.ACCESS_KEY} --secret_key=${env.SECRET_KEY} --acl-private put ${config.localArtifact} ${config.remoteArtifact} 
    sh '''
    echo ===============XXXXXXXX=
    '''
}
