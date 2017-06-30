import ai.h2o.ci.Utils

def utilsLib = new Utils()

def call(String title = "Environment") {
    sh """
       echo "\n=== ${title} ===\n"
       echo "\nJava version:\n$(java -version)"
       echo "\nEnvironment:\n$(env)"
       """
}

