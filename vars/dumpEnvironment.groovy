def call(String title = 'Environment') {
    def s = """
    echo ${title}
    $(java -version)
    """
    sh """
    echo -e "=== ${title} ==="
    ${s}
    """
    /*sh """
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """*/
}


