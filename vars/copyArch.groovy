import java.io.File

def call(String projectName, String buildId, String fileFilter) {
    call(projectName, buildId, fileFilter, true)
}

def call(String projectName, String buildId, String fileFilter, boolean rename) {
    step([$class: 'CopyArtifact',
        projectName: projectName,
        filter: fileFilter,
        selector: [$class: 'SpecificBuildSelector', buildNumber: buildId],
        optional: true
        ])
    // This needs a Pipeline utility plugin: https://github.com/jenkinsci/pipeline-utility-steps-plugin
    files = findFiles(glob: fileFilter)
    echo "Copy of ${files} from ${projectName}:${buildId} (rename: ${rename})"
    if (files.length > 0) {
        if (rename) {
            renameFiles(projectName.toUpperCase().replaceAll("/",""), files*.path.join(" "))
        }
    }
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
