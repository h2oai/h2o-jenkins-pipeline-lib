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

        stage'QA: build & lint'
          
                withEnv(["SPARK_HOME=${env.WORKSPACE}/${SPARK}","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",
                       "H2O_PYTHON_WHEEL=${env.WORKSPACE}/private/h2o.whl"]
                       ){
                        sh"""
                                mkdir -p ${env.WORKSPACE}/Rlibrary
                                echo "spark.driver.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"" >> ${env.SPARK_HOME}/conf/spark-defaults.conf
                                echo "spark.yarn.am.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"" >> ${env.SPARK_HOME}/conf/spark-defaults.conf
                                echo "spark.executor.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"" >> ${env.SPARK_HOME}/conf/spark-defaults.conf

                                echo "-Dhdp.version="${env.HDP_VERSION}"" >> ${env.SPARK_HOME}/conf/java-opts

                                mkdir -p ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/
                                cp /home/0xdiag/bigdata/laptop/citibike-nyc/2013-07.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-07.csv
                                cp /home/0xdiag/bigdata/laptop/citibike-nyc/2013-08.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-08.csv
                                cp /home/0xdiag/bigdata/laptop/citibike-nyc/2013-09.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-09.csv
                                cp /home/0xdiag/bigdata/laptop/citibike-nyc/2013-10.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-10.csv
                                cp /home/0xdiag/bigdata/laptop/citibike-nyc/2013-11.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-11.csv
                                cp /home/0xdiag/bigdata/laptop/citibike-nyc/2013-12.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/2013-12.csv
                                cp /home/0xdiag/bigdata/laptop/citibike-nyc/31081_New_York_City__Hourly_2013.csv ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/31081_New_York_City__Hourly_2013.csv


                                # Download h2o-python client, save it in private directory
                                # and export variable H2O_PYTHON_WHEEL driving building of pysparkling package
                                mkdir -p ${env.WORKSPACE}/private/
                                curl `./gradlew -q printH2OWheelPackage ` > ${env.WORKSPACE}/private/h2o.whl   

                        """                        
                }

        stage'QA:Unit tests'
        
                 withEnv(["SPARK_HOME=${env.WORKSPACE}/${SPARK}","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",
                       "H2O_PYTHON_WHEEL=${env.WORKSPACE}/private/h2o.whl"]
                       ){
                 sh """
                                # Build, run regular tests
                                if [ "$runBuildTests" = "true" ]; then
                                        ${env.WORKSPACE}/gradlew clean build -PbackendMode=${backendMode} 
                                else
                                        ${env.WORKSPACE}/gradlew clean build -x check -PbackendMode=${backendMode} 
                                fi

                                if [ "$runScriptTests" = "true" ]; then 
                                        ${env.WORKSPACE}/gradlew scriptTest -PbackendMode=${backendMode} 
                                fi  
                        """
                 }
        
        stage'QA:Integration tests'
        
                 withEnv(["SPARK_HOME=${env.WORKSPACE}/${SPARK}","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",
                       "H2O_PYTHON_WHEEL=${env.WORKSPACE}/private/h2o.whl"]
                       ){
                 sh """   
           
                        if [ "$runIntegTests" = "true" -a "$startH2OClusterOnYarn" = "true" ]; then 
                                echo "print******"
                                ${env.WORKSPACE}/gradlew integTest -PbackendMode=${backendMode} -PstartH2OClusterOnYarn -PsparklingTestEnv=$sparklingTestEnv -PsparkMaster=${env.MASTER} -PsparkHome=${env.SPARK_HOME} -x check -x :sparkling-water-py:integTest
                        fi 
                        if [ "$runIntegTests" = "true" -a "$startH2OClusterOnYarn" = "false" ]; then 
                                echo "print1*****111"
                                ${env.WORKSPACE}/gradlew integTest -PbackendMode=${backendMode} -PsparklingTestEnv=$sparklingTestEnv -PsparkMaster=${env.MASTER} -PsparkHome=${env.SPARK_HOME} -x check -x :sparkling-water-py:integTest
                        fi

                """
                 }
        
        
}
