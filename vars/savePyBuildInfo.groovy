import ai.h2o.ci.Utils

/**
 * Generate Python build information into a file given file.
 */
def call(boolean isRelease, String ...targetFile) {
    def utils = new Utils()
    def content = utils.getPyBuildInfo(isRelease)
    for(int i = 0; i < targetFile.length; i++) {
        writeFile file: targetFile[i], text: content
    }
}
