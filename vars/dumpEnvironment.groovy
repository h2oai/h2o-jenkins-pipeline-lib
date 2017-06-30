import ai.h2o.ci.Utils

def utilsLib = new Utils()

def call(String title = "Environment") {
    ansiColor('xterm') {
        sh """
           printf "\n=== ${title} ===\n"
           printf "\nJava version:\n$(java -version)"
           printf "\nEnvironment:\n$(env)"
           """
    }
}

