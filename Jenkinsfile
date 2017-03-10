node{
        stage 'Preparation' 
                git url: 'https://github.com/h2oai/sparkling-water.git'
                dumpEnvironment
                gradle.setCommonOptions(env.gradleCommonOptions)
                gradle.setEnv('SPARK_HOME', prepareSparkHome(env.WORKSPACE, env.sparkVersion, env.rsyncServer))
                gradle.setEnv('H2O_PYTHON_WHEEL', prepareH2oPythonWheel(env.WORKSPACE, envMap['SPARK_HOME']))
                echo 'Preparation done'
                

        stage'QA: build & lint'
            echo 'Testing again'
}
