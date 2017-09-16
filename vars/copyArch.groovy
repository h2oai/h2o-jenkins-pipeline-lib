def call(String projectName, String buildId, String fileFilter) {
    step([$class: 'CopyArtifact',
        projectName: projectName,
        filter: fileFilter,
        selector: [$class: 'SpecificBuildSelector', buildNumber: buildId]
        ])
    // THis needs a Pipeline utility plugin: https://github.com/jenkinsci/pipeline-utility-steps-plugin
    files = findFiles(glob: fileFilter)*.path.join(" ")
    setJUnitPrefix(projectName, files)
}

def setJUnitPrefix(prefix, files) {
  // add prefix to qualified classname
  //sh "shopt -s globstar && sed -i \"s/\\(<testcase .*classname=['\\\"]\\)\\([a-z]\\)/\\1${prefix.toUpperCase()}.\\2/g\" $files"
  sh "sed -i \"s/\\(<testcase classname=['\\\"]\\)\\(.\\)/\\1${prefix.toUpperCase()}.\\2/g\" $files"
}
