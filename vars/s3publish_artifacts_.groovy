def call(String project, String directoryOfMetaInfo, String directoryOfBuild, String branchName, String buildNumber){

    echo "********Parameters received by this function call********"
    echo "Project to publish: ${project}"
    echo "Directory of Meta information to publish: ${directoryOfMetaInfo}"
    echo "Directory of artifacts to publish: ${directoryOfBuild}"
    echo "Branch name: ${branchName}"
    echo "Build number: ${buildNumber}"
    //echo "Build number from env: ${env.BUILD_NUMBER}"

    //Publish the artifacts 
    sh "s3cmd --acl-public sync ${directoryOfBuild}/artifacts s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/"
    
    def list_of_publishable_files = sh (
        script: "find ${directoryOfBuild}/artifacts/ -name '*'",
        returnStdout: true).split("\n")
    
    try{
        upload_artifacts(list_of_publishable_files,directoryOfBuild,branchName,buildNumber)
    }
    catch(Exception e){
        echo "No Artifacts to upload"
    }
    
    echo "UPDATE LATEST POINTER"

    def tmpdir = "${directoryOfBuild}/datatable.tmp"
    sh """
        mkdir -p ${tmpdir}
        echo "${env.BUILD_NUMBER}" > ${tmpdir}/latest
        echo "<head>" > ${tmpdir}/latest.html
        echo "<meta http-equiv=\\"refresh\\" content=\\"0; url="${env.BUILD_NUMBER}"/index.html\\" />" >> ${tmpdir}/latest.html
        echo "</head>" >> ${tmpdir}/latest.html
        s3cmd --acl-public put ${tmpdir}/latest s3://ai.h2o.tests/intermittent_files/${branchName}/latest
        s3cmd --acl-public put ${tmpdir}/latest.html s3://ai.h2o.tests/intermittent_files/${branchName}/latest.html
        s3cmd --acl-public put ${tmpdir}/latest.html s3://ai.h2o.tests/intermittent_files/${branchName}/index.html
    """
}

@NonCPS
upload_artifacts(list_of_files,directoryOfBuild,branchName,buildNumber){
    for( f in list_of_files) {
        echo "${f}"
        length = "${f}".split("/").length
        name = "${f}".split("/")[length-1]
        echo "${name}"
        sh "s3cmd --acl-public put ${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${name}"
    }
    echo "Done"
}
