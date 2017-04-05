def call(String project, String files, String directoryOfBuild, String branchName, String buildNumber){

    echo "********Parameters received by this function call********"
    echo "Project to publish: ${project}"
    echo "Files to publish: ${files}"
    echo "Directory of artifacts to publish: ${directoryOfBuild}"

    // This is really awful.  This old version of s3cmd does not set Content-Encoding metadata in S3.
    // The newer version of s3cmd sets the Content-Encoding to UTF-8 and gradle fails.
    // Alternately, we could strip off the Content-Encoding metadata tag for every file underneath maven.

    //def list_of_files
    //def f

    sh "s3cmd --rexclude='${directoryOfBuild}/maven' --acl-public sync ${directoryOfBuild}/ s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/"

    echo "EXPLICITLY SET MIME TYPES AS NEEDED"

    // Process HTML files
    def list_of_html_files = sh (
            script: "find ${directoryOfBuild} -name '*.html' | sed 's/${directoryOfBuild}\\///g'",
            returnStdout: true).split("\n")
    println list_of_html_files

    upload_html(list_of_html_files,directoryOfBuild,branchName,buildNumber)

    // Process js files
    def list_of_js_files = sh (
            script: "find ${directoryOfBuild} -name '*.html' | sed 's/${directoryOfBuild}\\///g'",
            returnStdout: true).split("\n")
    println list_of_js_files

    upload_js(list_of_js_files,directoryOfBuild,branchName,buildNumber)

    // Process css files
    def list_of_css_files = sh (
            script: "find ${directoryOfBuild} -name '*.html' | sed 's/${directoryOfBuild}\\///g'",
            returnStdout: true).split("\n")
    println list_of_css_files

    upload_css(list_of_css_files,directoryOfBuild,branchName,buildNumber)

    echo "UPDATE LATEST POINTER"

    sh """
        tmpdir=./buildsparklingwater.tmp
        mkdir -p ${tmpdir}
        echo ${BUILD_NUMBER} > ${tmpdir}/latest
        echo "<head>" > ${tmpdir}/latest.html
        echo "<meta http-equiv=\\"refresh\\" content=\\"0; url=${BUILD_NUMBER}/index.html\\" />" >> ${tmpdir}/latest.html
        echo "</head>" >> ${tmpdir}/latest.html
        s3cmd --acl-public put ${tmpdir}/latest s3://h2o-release/sparkling-water/${BRANCH_NAME}/latest
        s3cmd --acl-public put ${tmpdir}/latest.html s3://h2o-release/sparkling-water/${BRANCH_NAME}/latest.html
        s3cmd --acl-public put ${tmpdir}/latest.html s3://h2o-release/sparkling-water/${BRANCH_NAME}/index.html

    """

}

@NonCPS
upload_html(list_of_files,directoryOfBuild,branchName,buildNumber){
    for( f in list_of_files) {
        echo "${f}"
        sh "s3cmd --acl-public --mime-type text/html put ${directoryOfBuild}/${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${f}"
    }
    echo "Done"
}


@NonCPS
upload_js(list_of_files,directoryOfBuild,branchName,buildNumber){
    for( f in list_of_files) {
        echo "${f}"
        sh "s3cmd --acl-public --mime-type text/javascript put ${directoryOfBuild}/${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${f}"
    }
    echo "Done"
}

@NonCPS
upload_css(list_of_files,directoryOfBuild,branchName,buildNumber){
    for( f in list_of_files) {
        echo "${f}"
        sh "s3cmd --acl-public --mime-type text/css put ${directoryOfBuild}/${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${f}"
    }
    echo "Done"
}

