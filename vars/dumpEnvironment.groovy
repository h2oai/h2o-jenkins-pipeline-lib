def call(String title = 'Environment') {
    sh """
    echo -e "=== ${title} ==="
    """
    /*sh """
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """*/
}


