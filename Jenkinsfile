pipeline {
    agent any

    stages {
        stage 'Preparation' 
            // Get code
            git url: 'https://github.com/h2oai/sparkling-water.git'
            // Show environment
            dumpEnvironment
            //
            // Setup gradle
            gradle.setCommonOptions(env.gradleCommonOptions)
            // Prepare Spark home
            gradle.setEnv('SPARK_HOME', prepareSparkHome(env.WORKSPACE, env.sparkVersion, env.rsyncServer))
            // Prepare H2O Wheel
            gradle.setEnv('H2O_PYTHON_WHEEL', prepareH2oPythonWheel(env.WORKSPACE, envMap['SPARK_HOME']))
     

        stage'QA: build & lint'
            def envList = toList(envMap)
            withEnv(envList) {
                echo "Build modules..."
                //gradle("--parallel clean build scalaStyle -x test -x integTest")
                gradle "clean build scalaStyle -x test -x integTest"
                // TODO: collect checkStyle results
            
        }

    }
}
