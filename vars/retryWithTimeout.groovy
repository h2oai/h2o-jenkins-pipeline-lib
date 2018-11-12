def call(int timeoutSeconds, int retries, body) {
    // DOES NOT WORK BECAUSE OF BUG IN JENKINS.
    // https://issues.jenkins-ci.org/browse/JENKINS-51454
    //
    // retry(retries) {
    //     timeout(time: timeoutSeconds, unit: 'SECONDS') {
    //         block()
    //     }
    // }

    def finished = false
    for (def i = 0; i < retries; i++) {
        if (finished) {
            break
        }
        try {
            timeout(time: timeoutSeconds, unit: 'SECONDS') {
                body()
                finished = true
            }
        } catch (Exception e) {
            if (i == (retries - 1)) {
                throw e
            }
            printTrace(e)
            e = null
        }
    }
}

@NonCPS
def printTrace(Exception e) {
    def sw = new StringWriter()
    e.printStackTrace(new PrintWriter(sw))
    echo sw.toString()
}
