def call(String project, String directoryOfMetaInfo, String directoryOfBuild, String branchName, String buildNumber){

    echo "********Parameters received by this function call********"
    echo "Project to publish: ${project}"
    echo "Directory of Meta information to publish: ${directoryOfMetaInfo}"
    echo "Directory of artifacts to publish: ${directoryOfBuild}"
    echo "Branch name: ${branchName}"
    echo "Build number: ${buildNumber}"

    //Publish the artifacts // > path | name=$(basename '$path' '.so') |echo "$name".so"
    
    sh "s3cmd --rexclude='${directoryOfBuild}/build/lib.linux-x86_64-3.6' --acl-public sync ${directoryOfBuild}/build/lib.linux-x86_64-3.6/ s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/"
    
    def list_of_publishable_files = sh (
        script: export path="$(find ${directoryOfBuild}/build/lib.linux-x86_64-3.6/ -name '*.so')";echo "${path##*/}",
        returnStdout: true).split("\n")
    println list_of_publishable_files
    
    try{
        upload_artifacts(list_of_publishable_files,directoryOfBuild,branchName,buildNumber)
    }
    catch(Exception e){
        echo "No files to upload"
    }
    
    //Publish meta information for the build
    sh "s3cmd --rexclude='${directoryOfBuild}/meta' --acl-public sync ${directoryOfBuild}/meta/ s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/"
    
    def list_of_publishable_meta_files = sh (
            script: "find ${directoryOfBuild}/meta -name '*.json'",
            returnStdout: true).split("\n")
    println list_of_publishable_meta_files
    
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
        sh "s3cmd --acl-public put ${directoryOfBuild}/build/lib.linux-x86_64-3.6/${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${f}"
    }
    echo "Done"
}

@NonCPS
upload_meta(list_of_files,directoryOfBuild,branchName,buildNumber){
    for( f in list_of_files) {
        echo "${f}"
        sh "s3cmd --acl-public put ${directoryOfBuild}/meta/${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${f}"
    }
    echo "Done"
}
