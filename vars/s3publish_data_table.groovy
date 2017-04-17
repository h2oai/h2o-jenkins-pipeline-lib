def call(String project, String directoryOfMetaInfo, String directoryOfBuild, String branchName, String buildNumber){

    echo "********Parameters received by this function call********"
    echo "Project to publish: ${project}"
    echo "Directory of Meta information to publish: ${directoryOfMetaInfo}"
    echo "Directory of artifacts to publish: ${directoryOfBuild}"
    echo "Branch name: ${branchName}"
    echo "Build number: ${buildNumber}"

    //Publish the artifacts // > path | name=$(basename '$path' '.so') |echo "$name".so"
    //--rexclude='${directoryOfBuild}/build/lib.linux-x86_64-3.6/datatable/*'
    //sh "s3cmd --acl-public sync ${directoryOfBuild}/build/lib.linux-x86_64-3.6/ s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/"
    //sh "s3cmd --acl-public put ${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${f}"
    
    def list_of_publishable_files = sh (
        script: "find ${directoryOfBuild}/build/lib.linux-x86_64-3.6/ -name '*.so'",
        returnStdout: true).split("\n")
    
    
    //try{
        upload_artifacts(list_of_publishable_files,directoryOfBuild,branchName,buildNumber)
   // }
   // catch(Exception e){
   //     echo "No files to upload"
   // }
    
    //Publish meta information for the build
    sh "s3cmd --acl-public sync ${directoryOfBuild}/meta/ s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/"
    
    def list_of_publishable_meta_files = sh (
            script: "find ${directoryOfBuild}/meta -name '*.json'",
            returnStdout: true).split("\n")
    
    upload_meta(list_of_publishable_meta_files,directoryOfBuild,branchName,buildNumber)

    echo "UPDATE LATEST POINTER"

    def tmpdir = "${directoryOfBuild}/datatable.tmp"
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
        sh "s3cmd --acl-public put ${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${name}"
    }
    echo "Done"
}
