package ai.h2o.ci

import ai.h2o.ci.buildsummary.BuildSummaryUtils

/**
 * Possible results of the stage or build
 */
enum BuildResult {
    /**
     * Indicates that stage is in progress
     */
    PENDING('Pending', 'nobuilt_anime.gif'),
    /**
     * Indicates that stage has been successful
     */
    SUCCESS('Success', 'green.gif'),
    /**
     * Indicates that stage has failed
     */
    FAILURE('Failure', 'red.gif'),
    /**
     * Indicates that stage has been successful HOWEVER the result needs attention.
     * For example the benchmarks are not in an expected interval/
     */
    WARNING('Warning', 'red.gif')

    private final String text
    private final String imageName

    BuildResult(final String text, final String imageName) {
        this.text = text
        this.imageName = imageName
    }

    String getText() {
        return text
    }

    String getImageUrl(final context, final imageSize) {
        return BuildSummaryUtils.imageLink(context, imageName, imageSize)
    }
}

