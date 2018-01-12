package ai.h2o.ci

/**
 * Possible results of the stage or build
 */
enum BuildResult {
    /**
     * Indicates that stage is in progress
     */
    PENDING('Pending'),
    /**
     * Indicates that stage has been successful
     */
    SUCCESS('Success'),
    /**
     * Indicates that stage has failed
     */
    FAILURE('Failure'),
    /**
     * Indicates that stage has been successful HOWEVER the result needs attention.
     * For example the benchmarks are not in an expected interval/
     */
    WARNING('Warning')

    private final String text

    BuildResult(final String text) {
        this.text = text
    }

    String getText() {
        return text
    }
}

