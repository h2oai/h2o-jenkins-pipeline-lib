def call(int timeoutSeconds, int retries, block) {
    retry(retries) {
        timeout(time: timeoutSeconds, unit: 'SECONDS') {
            block()
        }
    }
}
