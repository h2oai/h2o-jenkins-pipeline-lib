/**
 * Archive given list of artifacts.
 */
def call(String list) {
    archiveArtifacts artifacts: list, allowEmptyArchive: true
}

