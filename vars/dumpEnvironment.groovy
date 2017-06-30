def call(String title = "Environment") {
    sh """
       echo "\n=== Environment ==="
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """
}


