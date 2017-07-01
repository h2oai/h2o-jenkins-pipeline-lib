import ai.h2o.ci.Utils

def call(String title = 'Environment') {
    def utils = new Utils()

    echo """
    +===================+
      ${title}
    +===================+
    Git Describe: ${utils.gitDescribeAll()} 
    Java version: ${utils.javaVersion()}

    """

    /*sh """
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """*/
}


