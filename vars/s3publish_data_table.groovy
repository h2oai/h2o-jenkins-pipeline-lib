def call(String project, String directoryOfMetaInfo, String directoryOfBuild, String branchName){

    echo "********Parameters received by this function call********"
    echo "Project to publish: ${project}"
    echo "Directory of Meta information to publish: ${directoryOfMetaInfo}"
    echo "Directory of artifacts to publish: ${directoryOfBuild}"
    echo "Branch name: ${branchName}"
    //echo "Build number: ${buildNumber}"
    echo "Build number from env: ${env.BUILD_NUMBER}"

    //Publish the artifacts 
    sh "s3cmd --acl-public sync ${directoryOfBuild}/build/lib.linux-x86_64-3.6/*.so --rexclude='${directoryOfBuild}/build/lib.linux-x86_64-3.6/datatable/*' s3://ai.h2o.tests/intermittent_files/${branchName}/${env.BUILD_NUMBER}/"
    
    def list_of_publishable_files = sh (
        script: "find ${directoryOfBuild}/build/lib.linux-x86_64-3.6/ -name '*.so'",
        returnStdout: true).split("\n")
    
    
    try{
        upload_artifacts(list_of_publishable_files,directoryOfBuild,branchName,"${env.BUILD_NUMBER}")
    }
    catch(Exception e){
        echo "No Artifacts to upload"
    }
    
    //Publish meta information for the build
    sh "s3cmd --acl-public sync ${directoryOfBuild}/meta/ s3://ai.h2o.tests/intermittent_files/${branchName}/${env.BUILD_NUMBER}/meta/"
    
    def list_of_publishable_meta_files = sh (
            script: "find ${directoryOfBuild}/meta -name '*.json'",
            returnStdout: true).split("\n")
    
    try{
    upload_meta(list_of_publishable_meta_files,directoryOfBuild,branchName,"${env.BUILD_NUMBER}")
    }
    catch(Exception e){
        echo "No meta files to upload"
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

@NonCPS
upload_meta(list_of_files,directoryOfBuild,branchName,buildNumber){
    for( f in list_of_files) {
        echo "${f}"
        length = "${f}".split("/").length
        name = "${f}".split("/")[length-1]
        echo "${name}"
        sh "s3cmd --acl-public put ${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/meta/${name}"
    }
    echo "Done"
}

