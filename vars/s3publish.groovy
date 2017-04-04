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

    //def list_of_files
    //def f

    sh "s3cmd --rexclude='${directoryOfBuild}/maven' --acl-public sync ${directoryOfBuild}/ s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/"
    
   echo "EXPLICITLY SET MIME TYPES AS NEEDED"
    
    def list_of_files = sh (
        script: "find ${directoryOfBuild} -name '*.html' | sed 's/${directoryOfBuild}\\///g'",
        returnStdout: true)
    println list_of_files
    
    echo "TEST"
    
    for( def f in list_of_files) {
        println f
        //sh "s3cmd --acl-public --mime-type text/html put ${directoryOfBuild}/${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${f}"
    }
    
 

/*
        list_of_files=`find ${directoryOfBuild} -name '*.js' | sed 's/${directoryOfBuild}\\///g'`
        echo ${list_of_files}
        for f in ${list_of_files}
        do
            s3cmd --acl-public --mime-type text/javascript put ${directoryOfBuild}/${f} s3://ai.h2o.tests/intermittent_files/${branchName}/$buildNumber}/${f}
        done

        list_of_files=`find ${directoryOfBuild} -name '*.css' | sed 's/${directoryOfBuild}\\///g'`
        echo ${list_of_files}
        for f in ${list_of_files}
        do
            s3cmd --acl-public --mime-type text/css put ${directoryOfBuild}/${f} s3://ai.h2o.tests/intermittent_files/${branchName}/${buildNumber}/${f}
        done

        echo "UPDATE LATEST POINTER"

*/


    //branchdir , THIS_DIR, IS_LATEST_STABLE
    //Which node to pick the artifacts from? or WHERE

    ///Users/nikhilshekhar/pipeline/vars/s3publish.groovy

}
