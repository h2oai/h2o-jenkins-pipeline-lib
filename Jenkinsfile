#!/usr/bin/groovy

node{
        stage 'Preparation'
                git url: 'https://github.com/h2oai/sparkling-water.git'
                def SPARK="spark-${sparkVersion}-bin-hadoop2.6"
                def SPARKTGZ=$SPARK.tgz
                sh "wget http://d3kbcqa49mib13.cloudfront.net/${SPARK}.tgz"
                echo 'Preparation done'        

        stage'QA: build & lint'
            echo 'Testing again'
        
}
