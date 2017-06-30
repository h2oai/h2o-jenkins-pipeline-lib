import ai.h2o.ci.Utils

def call(String title = 'Environment') {
    def utils = new Utils()

    def s = """echo ${title}
    $(java -version)
    """

    /*sh """
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """*/
}


