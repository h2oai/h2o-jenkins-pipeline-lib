/**
 * Invokes s3up with specified body inside specified container from given registry.
 * @param registry docker registry to use
 * @param image docker image to use
 * @param body body used by s3cmd
 * @return
 */
def call(String registry, String image, body) {
    docker.withRegistry("https://${registry}", "${registry}") {
        docker.image("${registry}/${image}").inside {
            s3up(body)
        }
    }

}

/**
 * Invokes s3up with specified body inside docker.h2o.ai/s3cmd docker container.
 * @param body body used by s3cmd
 * @return
 */
def call(body) {
    call('harbor.h2o.ai', 's3cmd', body)
}
