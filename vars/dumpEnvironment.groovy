//import ai.h2o.ci.Utils

//def utilsLib = new Utils()

def call(String title = "Environment") {
    sh """
       echo -e "\n=== ${title} ===\n"
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """
}

