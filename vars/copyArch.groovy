import java.io.File

def call(String projectName, String buildId, String fileFilter) {
    step([$class: 'CopyArtifact',
        projectName: projectName,
        filter: fileFilter,
        selector: [$class: 'SpecificBuildSelector', buildNumber: buildId]
        ])
    // THis needs a Pipeline utility plugin: https://github.com/jenkinsci/pipeline-utility-steps-plugin
    files = findFiles(glob: fileFilter)*.path.join(" ")
    renameFiles(projectName.toUpperCase().replaceAll("/",""), files)
}

def renameFiles(prefix, files) {
    sh """
    for f in ${files}; do
        fname=`basename \$f`
        dir=`dirname \$f`
        nfname="\$dir/${prefix}-\$fname"
        mv "\$f" "\$nfname"
    done
    """
}
