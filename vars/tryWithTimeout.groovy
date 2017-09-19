def call(int timeoutSeconds, int retries, block) {
    retry(retries) {
        timeout(timeoutSeconds, unit: SECONDS) {
            block()
        }
    }
}
