#!/usr/bin/groovy

node('mr-0xc2'){
        stage 'Preparation'
                
                git url: 'https://github.com/h2oai/sparkling-water.git'
                def SPARK="spark-${sparkVersion}-bin-hadoop2.6"
                def SPARKTGZ="${SPARK}.tgz"
                sh "wget http://d3kbcqa49mib13.cloudfront.net/${SPARK}.tgz"
                echo "Extracting spark JAR"
                sh "tar zxvf ${SPARKTGZ}"
                echo 'Preparation done'  
        
                withEnv(["SPARK_HOME=${env.WORKSPACE}/${SPARK}","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client',R_LIBS_USER=${env.WORKSPACE}/Rlibrary,HDP_VERSION=${hdpVersion}"]){
                        sh"""echo "SPARK:*****"
                                echo ${SPARK}
                                echo "Spark home*****"
                                 echo ${env.SPARK_HOME}
                                echo "Spark home*****"
                                 echo ${env.HADOOP_CONF_DIR}
                                 echo ${env.HDP_VERSION}
                                 mkdir -p ${env.WORKSPACE}/Rlibrary
                                 cat <<EOF > ${env.SPARK_HOME}/conf/spark-defaults.conf
                                 spark.driver.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"
                                 spark.yarn.am.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"
                                 spark.executor.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"
                                 EOF

                                cat <<EOF > ${env.SPARK_HOME}/conf/java-opts
                                -Dhdp.version="${env.HDP_VERSION}"
                                EOF

                                mkdir -p ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/
                                for f in 2013-07.csv 2013-08.csv 2013-09.csv 2013-10.csv 2013-11.csv 2013-12.csv 31081_New_York_City__Hourly_2013.csv
                                do
                                        echo "f******"
                                        echo $f
                                        cp /home/0xdiag/bigdata/laptop//citibike-nyc/$f ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/$f
                                done

                           
                        """
                        
                }

        stage'QA: build & lint'
            echo 'Testing again'
        
}
