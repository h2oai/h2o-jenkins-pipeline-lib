#!/usr/bin/groovy

node('mr-0xc2'){
        stage 'Preparation'
                
                git url: 'https://github.com/h2oai/sparkling-water.git'
                def SPARK="spark-${sparkVersion}-bin-hadoop2.6"
                sh"""
                if [ ! -d "${SPARK}" ]; then
                        wget "http://d3kbcqa49mib13.cloudfront.net/${SPARK}.tgz"
                        echo "Extracting spark JAR"
                        tar zxvf ${SPARK}.tgz
                fi
                sh"""
                echo 'Preparation done'  
                

        stage'QA: build & lint'
          
                withEnv(["SPARK_HOME=${env.WORKSPACE}/${SPARK}","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",
                       "H2O_PYTHON_WHEEL=${env.WORKSPACE}/private/h2o.whl","H2O_EXTENDED_JAR=${env.WORKSPACE}/assembly-h2o/private/"]
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
                                ./gradlew -q extendJar -PdownloadH2O=${env.driverHadoopVersion}
                                 

                        """   
                        echo 'Archiving artifacts after build'
                        archive includes:'**/build/*tests.log,**/*.log, **/out.*, **/*py.out.txt,examples/build/test-results/binary/integTest/*, **/stdout, **/stderr,**/build/**/*log*, py/build/py_*_report.txt,**/build/reports/'
                }

        stage'QA:Unit tests'
        
                 withEnv(["SPARK_HOME=${env.WORKSPACE}/${SPARK}","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",
                       "H2O_PYTHON_WHEEL=${env.WORKSPACE}/private/h2o.whl","H2O_EXTENDED_JAR=${env.WORKSPACE}/assembly-h2o/private/"]
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
                         echo 'Archiving artifacts after Unit tests'
                         archive includes:'**/build/*tests.log,**/*.log, **/out.*, **/*py.out.txt,examples/build/test-results/binary/integTest/*, **/stdout, **/stderr,**/build/**/*log*, py/build/py_*_report.txt,**/build/reports/'
                         echo "Stashing the Entire repository"
                         stash name: "unit-test-stash", includes: "${env.WORKSPACE}/*"
                         
                 }
             
}
