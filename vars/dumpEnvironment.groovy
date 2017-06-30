import ai.h2o.ci.Utils

def call(String title = 'Environment') {
    def utils = new Utils()

    echo """
    +===================+
      ${title}
    +===================+
    """

    /*sh """
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """*/
}


