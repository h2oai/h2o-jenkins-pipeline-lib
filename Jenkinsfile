node{
        stage 'Preparation'
                git url: 'https://github.com/h2oai/sparkling-water.git'
                //gradle.setCommonOptions(env.gradleCommonOptions)
                //gradle.setEnv('SPARK_HOME', prepareSparkHome(env.WORKSPACE, env.sparkVersion, env.rsyncServer))
                //gradle.setEnv('H2O_PYTHON_WHEEL', prepareH2oPythonWheel(env.WORKSPACE, envMap['SPARK_HOME']))
                echo 'Preparation done'        

        stage'QA: build & lint'
            echo 'Testing again'
        
}

def prepareSparkHome(workspaceDir, sparkVersion, rsyncServer) {
    def sparkDirName = "spark-${sparkVersion}-bin-hadoop2.6"
    def sparkFileName = "${sparkDirName}.tgz"
    def destFile = "${workspaceDir}/${sparkFileName}"
    def destDir = "${workspaceDir}/${sparkDirName}"
    if (!fileExists(destDir)) {
        if (!fileExists(destFile)) {
            //sh "sshpass -p \'rsync\' rsync -aq --stats rsync://rsync@${rsyncServer}/Download/spark/${sparkFileName} ${workspaceDir}/${sparkFileName}"
            sh "wget -q http://d3kbcqa49mib13.cloudfront.net/${sparkFileName}"
        }
        sh "tar zxvf ${destFile}"
    }
    dir("$destDir/work") {
        deleteDir()
    }
    return destDir
}
