import ai.h2o.ci

def call(String title = "Environment") {
    ansiColor('xterm') {
        sh '''
           printf '\n\001B[32m=== ${title} ===\001B[0m\n'
           printf "\nJava version:\n$(java -version)"
           printf "\nEnvironment:\n$(env)"
           '''
    }
}


