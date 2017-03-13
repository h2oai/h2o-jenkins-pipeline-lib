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
                sh"""
                if [ "${startH2OClusterOnYarn}" == "true" ]; then
                def START_CLUSTER_ON_YARN="-PstartH2OClusterOnYarn"
                else
                def START_CLUSTER_ON_YARN=""
                fi"""
                
                withEnv(["SPARK_HOME=${env.WORKSPACE}/${SPARK}","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",
                       "H2O_PYTHON_WHEEL=${env.WORKSPACE}/private/h2o.whl"]
                       ){
                        sh"""echo "SPARK:*****"
                                echo ${SPARK}
                                echo "Spark home*****"
                                 echo ${env.SPARK_HOME}
                                echo "Spark home*****"
                                 echo ${env.HADOOP_CONF_DIR}
                                 echo ${env.HDP_VERSION}
                                 mkdir -p ${env.WORKSPACE}/Rlibrary
                                 echo "spark.driver.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"" >> ${env.SPARK_HOME}/conf/spark-defaults.conf
                                 echo "spark.yarn.am.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"" >> ${env.SPARK_HOME}/conf/spark-defaults.conf
                                 echo "spark.executor.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"" >> ${env.SPARK_HOME}/conf/spark-defaults.conf

                                 echo "-Dhdp.version="${env.HDP_VERSION}"" >> ${env.SPARK_HOME}/conf/java-opts



                                mkdir -p ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/
                                cp /home/0xdiag/bigdata/laptop//citibike-nyc/2013-07.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-07.csv
                                cp /home/0xdiag/bigdata/laptop//citibike-nyc/2013-08.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-08.csv
                                cp /home/0xdiag/bigdata/laptop//citibike-nyc/2013-09.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-09.csv
                                cp /home/0xdiag/bigdata/laptop//citibike-nyc/2013-10.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-10.csv
                                cp /home/0xdiag/bigdata/laptop//citibike-nyc/2013-11.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-11.csv
                                cp /home/0xdiag/bigdata/laptop//citibike-nyc/2013-12.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-12.csv
                                cp /home/0xdiag/bigdata/laptop//citibike-nyc/31081_New_York_City__Hourly_2013.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/31081_New_York_City__Hourly_2013.csv


                                # Download h2o-python client, save it in private directory
                                # and export variable H2O_PYTHON_WHEEL driving building of pysparkling package
                                mkdir -p ${env.WORKSPACE}/private/

                                echo "workspace******"
                                echo ${env.WORKSPACE}
                                curl `./gradlew -q printH2OWheelPackage ` > ${env.WORKSPACE}/private/h2o.whl

                                #
                                # Build, run regular tests
                                #
                                if [ "$runBuildTests" == "true" ]; then
                                $WORKSPACE/gradlew clean build -PbackendMode=${backendMode} 
                                else
                                $WORKSPACE/gradlew clean build -x check -PbackendMode=${backendMode} 
                                fi

                                if [ "$runScriptTests" == "true" ]; then 
                                $WORKSPACE/gradlew scriptTest -PbackendMode=${backendMode} 
                                fi
                                
 
                        """
                        
                }

        stage'QA: build & lint'
            echo 'Testing again'
        
}
