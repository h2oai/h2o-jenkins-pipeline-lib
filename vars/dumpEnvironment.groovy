import ai.h2o.ci.Utils

def call(String title = 'Environment') {
    def utils = new Utils()

    echo """
    +===================+
      ${title}
    +===================+
    Git Describe: ${utils.gitDescribeAll()} / ${utils.gitDescribe()}
    Java version: ${utils.getJavaVersion()}

    """

    /*sh """
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """*/
}


