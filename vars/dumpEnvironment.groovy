import ai.h2o.build

def call(String title = "Environment") {
    ansiColor('xterm') {
        sh '''
           printf "\n\e[32m=== ${title} ===\e[0m\n"
           printf "\nJava version:\n$(java -version)"
           printf "\nEnvironment:\n$(env)"
           '''
    }
}


