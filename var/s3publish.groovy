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

    sh """

        s3cmd --rexclude='${directoryOfBuild}/maven' --acl-public sync ${directoryOfBuild}/ s3://h2o-release/h2o/${branchName}/${buildNumber}/

        ~jenkins/frozen/s3cmd-1.0.1/s3cmd --acl-public sync ${directoryOfBuild}/maven/ s3://h2o-release/h2o/${branchName}/${buildNumber}/maven/

        echo "EXPLICITLY SET MIME TYPES AS NEEDED"

        list_of_html_files= `find ${directoryOfBuild} -name '*.html | sed 's/${directoryOfBuild}\\///g''`
        echo ${list_of_html_files}
        for f in ${list_of_html_files}
        do
            s3cmd --acl-public --mime-type text/html put ${directoryOfBuild}/${f} s3://h2o-release/h2o/${branchName}/${buildNumber}/${f}
        done

        list_of_js_files=`find ${directoryOfBuild} -name '*.js' | sed 's/${directoryOfBuild}\\///g'`
        echo ${list_of_js_files}
        for f in ${list_of_js_files}
        do
            s3cmd --acl-public --mime-type text/javascript put ${directoryOfBuild}/${f} s3://h2o-release/h2o/${branchName}/$buildNumber}/${f}
        done
        
        list_of_css_files=`find ${directoryOfBuild} -name '*.css' | sed 's/${directoryOfBuild}\\///g'`
        echo ${list_of_css_files}
        for f in ${list_of_css_files}
        do
            s3cmd --acl-public --mime-type text/css put ${directoryOfBuild}/${f} s3://h2o-release/h2o/${branchName}/${buildNumber}/${f}
        done

        echo "UPDATE LATEST POINTER"

        tmpdir=./buildh2odev.tmp
        mkdir -p ${tmpdir}
        echo ${buildNumber} > ${tmpdir}/latest
        echo "<head>" > ${tmpdir}/latest.html
        echo "<meta http-equiv=\\"refresh\\" content=\\"0; url=${buildNumber}/index.html\\" />" >> ${tmpdir}/latest.html
        echo "</head>" >> ${tmpdir}/latest.html

        echo "PUSH TO S3"

        s3cmd --acl-public put ${tmpdir}/latest s3://h2o-release/h2o/${branchName}/latest
        s3cmd --acl-public put ${tmpdir}/latest.html s3://h2o-release/h2o/${branchName}/latest.html
        s3cmd --acl-public put ${tmpdir}/latest.html s3://h2o-release/h2o/${branchName}/index.html

      #  projectVersion =\$(cat ${directoryOfBuild}/project_version)

      # if [ "$IS_LATEST_STABLE" = "true" ]; then
      #     $THIS_DIR/h2o3-upload-stable-links.sh "$branchName" "$buildNumber" "$projectVersion"
      # fi
        
        
        # Update success pointer.
        cd ${branchdir}
        rm -f lastSuccessfulBuild
        ln -s ${buildNumber} lastSuccessfulBuild
    """


    //branchdir , THIS_DIR, IS_LATEST_STABLE
    //Which node to pick the artifacts from?
    //Call to this:
    //s3publish 'project' 'files' 'directoryOfBuild' 'branchName' 'buildNumber'
    ///Users/nikhilshekhar/pipeline/vars/s3publish.groovy

}
