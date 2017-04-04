/**
 * Created by nikhilshekhar on 4/4/17.
 */


def call(String project, String files, String directoryOfBuild, String branchName, String buildNumber){

    echo "Parameters received by this function call:"
    echo "Project to publish: ${project}"
    echo "Files to publish: ${files}"
    echo "Directory of artifacts to publish: ${directoryOfBuild}"

    // This is really awful.  This old version of s3cmd does not set Content-Encoding metadata in S3.
    // The newer version of s3cmd sets the Content-Encoding to UTF-8 and gradle fails.
    // Alternately, we could strip off the Content-Encoding metadata tag for every file underneath maven.
    def list_of_html_files
    def list_of_js_files
    def list_of_css_files
    def f
 

    //sh  "s3cmd --rexclude='${directoryOfBuild}/maven' --acl-public sync ${directoryOfBuild}/ s3://h2o-release/h2o/${branchName}/${buildNumber}/"

    //sh  "~jenkins/frozen/s3cmd-1.0.1/s3cmd --acl-public sync ${directoryOfBuild}/maven/ s3://h2o-release/h2o/${branchName}/${buildNumber}/maven/"
    
    echo "EXPLICITLY SET MIME TYPES AS NEEDED"

    sh "list_of_html_files= `find ${directoryOfBuild} -name '*.html | sed 's/${directoryOfBuild}\\///g''`"
    
    echo "${list_of_html_files}"
    
    sh """
        for f in ${list_of_html_files}
        do
            echo "Inside for"
            s3cmd --acl-public --mime-type text/html put ${directoryOfBuild}/${f} s3://h2o-release/h2o/${branchName}/${buildNumber}/${f}
        done
    
    """

}
