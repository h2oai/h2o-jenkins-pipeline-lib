def call(int timeoutSeconds, int retries, block) {
    // DOES NOT WORK BECAUSE OF BUG IN JENKINS.
    // https://issues.jenkins-ci.org/browse/JENKINS-51454
    //
    // retry(retries) {
    //     timeout(time: timeoutSeconds, unit: 'SECONDS') {
    //         block()
    //     }
    // }
    
    for (def i = 0; i < retries; i++) {
        try {
            timeout(time: timeoutSeconds, unit: 'SECONDS') {
                block()
                break
            }
        } catch (Exception e) {
            if (i == (retries - 1)) {
                throw e
            }
            def sw = new StringWriter()
            e.printStackTrace(new PrintWriter(sw))
            echo sw.toString()
        }
    }
}
