#!/usr/bin/groovy

node('mr-0xc2'){
        stage 'Preparation'
                
                git url: 'https://github.com/h2oai/sparkling-water.git'
                def SPARK="spark-${sparkVersion}-bin-hadoop2.6"
                def SPARKTGZ="${SPARK}.tgz"
                sh "wget http://d3kbcqa49mib13.cloudfront.net/${SPARK}.tgz"
                echo 'Preparation done'  
                withEnv(["SPARK_HOME=cd ${SPARK};pwd","HADOOP_CONF_DIR=/etc/hadoop/conf"]){
                        sh"""echo ${env.BRANCH_NAME}
                             echo ${env.SPARK_HOME}
                             echo ${env.HADOOP_CONF_DIR}
                        """
                        
                }

        stage'QA: build & lint'
            echo 'Testing again'
        
}
