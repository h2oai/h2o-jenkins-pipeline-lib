/**
 * Collect test reports from given directory.
 */
def call(reportDirectory, title) {
    publishHTML target: [
        allowMissing: false,
        alwaysLinkToLastBuild: true,
        keepAll: true,
        reportDir: reportDirectory,
        reportFiles: 'index.html',
        reportName: title
    ]
}

