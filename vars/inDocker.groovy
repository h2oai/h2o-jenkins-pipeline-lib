/**
 * Invokes give body inside specified container from given registry.
 * @param registry docker registry to use
 * @param image docker image to use
 * @param body  freestyle commands
 * @return
 */
def call(String registry, String image, body) {
    docker.withRegistry("https://${registry}", "${registry}") {
        docker.image("${registry}/${image}").inside {
            body()
        }
    }

}

/**
 * Invokes given body inside docker.h2o.ai/s3cmd docker container.
 * @param body  freestyle commands
 * @return
 */
def call(image, body) {
    call('docker.h2o.ai', image, body)
}
