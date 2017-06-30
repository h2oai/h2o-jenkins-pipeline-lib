import ai.h2o.ci.Utils

def call(String title = 'Environment') {
    def utils = new Utils()

    echo """
    \033[1;33m
    +===================+
      ${title}
    +===================+\033[0m
    Git Describe : ${utils.gitDescribe()}
    Git Describe : ${utils.gitDescribeAll()}
    Git Branch   : ${utils.gitBranch()}
    Java version : ${utils.javaVersion()}
    """

    /*sh """
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """*/
}


