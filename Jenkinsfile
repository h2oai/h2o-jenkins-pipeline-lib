#!/usr/bin/groovy

node('mr-0xc2'){
        stage 'Preparation'
                git url: 'https://github.com/h2oai/sparkling-water.git'
                def SPARK="spark-${sparkVersion}-bin-hadoop2.6"
                def SPARKTGZ="${SPARK}.tgz"
                sh "wget http://d3kbcqa49mib13.cloudfront.net/${SPARK}.tgz"
                echo 'Preparation done'   
                export SPARK_HOME=$(cd $SPARK; pwd)
                echo "${SPARK_HOME}"

        stage'QA: build & lint'
            echo 'Testing again'
        
}
