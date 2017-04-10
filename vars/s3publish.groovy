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
    
    try{
        upload_html(list_of_html_files,directoryOfBuild,branchName,buildNumber)
        catch(Exception e){
            echo "No HTML files to upload"
        }
    }
    
    // Process js files
    def list_of_js_files = sh (
            script: "find ${directoryOfBuild} -name '*.html' | sed 's/${directoryOfBuild}\\///g'",
            returnStdout: true).split("\n")
    println list_of_js_files
    
    try{
        upload_js(list_of_js_files,directoryOfBuild,branchName,buildNumber)
        catch(Exception e){
            echo "No JS files to upload"
        }
    }
    
    // Process css files
    def list_of_css_files = sh (
            script: "find ${directoryOfBuild} -name '*.html' | sed 's/${directoryOfBuild}\\///g'",
            returnStdout: true).split("\n")
    println list_of_css_files
    
    try{
        upload_css(list_of_css_files,directoryOfBuild,branchName,buildNumber)
        catch(Exception e){
            echo "No CSS files to upload"
        }
    }

    echo "UPDATE LATEST POINTER"

    def tmpdir = "./buildsparklingwater.tmp"
    sh """
        mkdir -p ${tmpdir}
        echo ${buildNumber} > ${tmpdir}/latest
        echo "<head>" > ${tmpdir}/latest.html
        echo "<meta http-equiv=\\"refresh\\" content=\\"0; url=${buildNumber}/index.html\\" />" >> ${tmpdir}/latest.html
        echo "</head>" >> ${tmpdir}/latest.html
        s3cmd --acl-public put ${tmpdir}/latest s3://ai.h2o.tests/intermittent_files/${branchName}/latest
        s3cmd --acl-public put ${tmpdir}/latest.html s3://ai.h2o.tests/intermittent_files/${branchName}/latest.html
        s3cmd --acl-public put ${tmpdir}/latest.html s3://ai.h2o.tests/intermittent_files/${branchName}/index.html

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

