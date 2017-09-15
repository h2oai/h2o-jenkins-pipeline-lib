def call(String projectName, String buildId, String fileFilter) {
    step([$class: 'CopyArtifact',
        projectName: projectName,
        filter: fileFilter,
        selector: [$class: 'SpecificBuildSelector', buildNumber: buildId]
        ])
}

