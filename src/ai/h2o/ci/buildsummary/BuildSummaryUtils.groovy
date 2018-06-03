package ai.h2o.ci.buildsummary

class BuildSummaryUtils {

     /**
     * Returns absolute URL for the given image of given size
     * @param context
     * @param imageName name of the image
     * @param imageSize size of the image
     * @return
     */
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