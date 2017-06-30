def call(String title = 'Environment') {
    echo "=== ${title} ==="
    sh """
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """
}


