def call(final int retries, final int delay, final Closure body) {
    for (def i = 0; i < retries; i++) {
        try {
            body()
            break
        } catch (Exception e) {
            if (i == (retries - 1)) {
                throw e
            }
            script.sleep(delay)
        }
    }
}
