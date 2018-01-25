package ai.h2o.ci.buildsummary

import ai.h2o.ci.BuildResult

class BuildSummaryUtils {


    static String stageResultToImageName(final BuildResult result) {
        switch (result) {
            case BuildResult.PENDING:
                return 'nobuilt_anime.gif'
            case BuildResult.FAILURE:
                return 'red.gif'
            case BuildResult.SUCCESS:
                return 'green.gif'
            default:
                return 'red.gif'
        }
    }

    static String imageLink(final context, final String imageName, final ImageSize imageSize = ImageSize.MEDIUM) {
        "${context.env.HUDSON_URL}${Jenkins.RESOURCE_PATH}/images/${imageSize.getSizeString()}/${imageName}"
    }

    static enum ImageSize {
        SMALL('16x16'),
        MEDIUM('24x24'),
        LARGE('32x32'),
        XLARGE('48x48')

        private final String size

        private ImageSize(final String size) {
            this.size = size
        }

        String getSizeString() {
            return size
        }
    }
}