#!/usr/bin/groovy


node('mr-0xd2'){
        
        stage('init'){
                def SPARK="spark-${sparkVersion}-bin-hadoop2.6"
        }
        
        stage('Git Checkout and Preparation'){
                
                git url: 'https://github.com/h2oai/sparkling-water.git'
                def SPARK="spark-${sparkVersion}-bin-hadoop2.6"
                sh"""
                if [ ! -d "${SPARK}" ]; then
                        wget "http://d3kbcqa49mib13.cloudfront.net/${SPARK}.tgz"
                        echo "Extracting spark JAR"
                        tar zxvf ${SPARK}.tgz
                fi
                sh"""
                echo 'Checkout and Preparation completed'  
        }
                
        stage('QA: build & lint'){
                
                withEnv(["SPARK_HOME=${env.WORKSPACE}/spark-2.1.0-bin-hadoop2.6","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",
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
                        //echo 'Archiving artifacts after build'
                        //archive includes:'**/build/*tests.log,**/*.log, **/out.*, **/*py.out.txt,examples/build/test-results/binary/integTest/*, **/stdout, **/stderr,**/build/**/*log*, py/build/py_*_report.txt,**/build/reports/'
                        
               }
        }

        
        stage('QA:Unit tests'){
        
                withEnv(["SPARK_HOME=${env.WORKSPACE}/spark-2.1.0-bin-hadoop2.6","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",
                       "H2O_PYTHON_WHEEL=${env.WORKSPACE}/private/h2o.whl","H2O_EXTENDED_JAR=${env.WORKSPACE}/assembly-h2o/private/"]
                       ){
                     sh """
                                # Build, run regular tests
                                if [ "$runBuildTests" = true ]; then
                                        echo 'runBuildTests = True'
                                       ${env.WORKSPACE}/gradlew clean build -PbackendMode=${backendMode} 
                                else
                                        echo 'runBuildTests = False'
                                        ${env.WORKSPACE}/gradlew clean build -x check -PbackendMode=${backendMode} 
                                        
                                fi

                                if [ "$runScriptTests" = true ]; then 
                                        echo 'runScriptTests = true'
                                        ${env.WORKSPACE}/gradlew scriptTest -PbackendMode=${backendMode} 
                                        
                                fi  
                                
                                # running integration just after unit test
                                ${env.WORKSPACE}/gradlew integTest -PbackendMode=${backendMode} -PsparklingTestEnv=$sparklingTestEnv -PsparkMaster=${env.MASTER} -PsparkHome=${env.SPARK_HOME} -x check -x :sparkling-water-py:integTest		
                        """
                         //echo 'Archiving artifacts after Unit tests'
                         //archive includes:'**/build/*tests.log,**/*.log, **/out.*, **/*py.out.txt,examples/build/test-results/binary/integTest/*, **/stdout, **/stderr,**/build/**/*log*, py/build/py_*_report.txt,**/build/reports/'
                         
                 }
        }
        
        stage('Stashing'){
                
               /* try{
                // Make a tar of the directory and stash it
                sh "tar --ignore-failed-read -zcvf stash_archive.tar.gz ."
                }
                catch(err){
                        echo "Tar completed with few changes while running tar"
                }*/
                stash name: 'unit-test-stash', includes: 'stash_archive.tar.gz'
                echo 'Stash successful'
                
                sh "ls -ltrh ${env.WORKSPACE}"
                echo "Deleting the original workspace after stashing the directory"
                sh "rm -r ${env.WORKSPACE}/*"
                echo "Workspace Directory deleted"
                
        }

        
        stage('QA:Integration tests'){
                 		
                 echo "Unstash the unit test"		
         		
                // dir("unit-test-stash") {	
                        
                         unstash "unit-test-stash"
                
                        //Untar the archieve
                         //sh "tar -zxvf stash_archive.tar.gz"
                         sh "ls -ltrh ${env.WORKSPACE}"
                         /* sh """
                                # Move the unstashed directory outside the stashed one for the environment variables to pick up properly
                                 mv ${env.WORKSPACE}/unit-test-stash/* ${env.WORKSPACE}
                          
                                 #rm -r ${env.WORKSPACE}/unit-test-stash

                           """
*/                 //}		
         		
                withEnv(["SPARK_HOME=${env.WORKSPACE}/spark-2.1.0-bin-hadoop2.6","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",		
                        "H2O_PYTHON_WHEEL=${env.WORKSPACE}/private/h2o.whl","H2O_EXTENDED_JAR=${env.WORKSPACE}/assembly-h2o/private/"]		
                        ){	
                        try{
                                
                                sh """  
                                 if [ "$runIntegTests" = true -a "$startH2OClusterOnYarn" = true ]; then 
                                         ${env.WORKSPACE}/gradlew integTest -PbackendMode=${backendMode} -PstartH2OClusterOnYarn -PsparklingTestEnv=$sparklingTestEnv -PsparkMaster=${env.MASTER} -PsparkHome=${env.SPARK_HOME} -x check -x :sparkling-water-py:integTest		
                                 fi 
        
                                 if [ "$runIntegTests" = true -a "$startH2OClusterOnYarn" = false ]; then 		
                                        ${env.WORKSPACE}/gradlew integTest -PbackendMode=${backendMode} -PsparklingTestEnv=$sparklingTestEnv -PsparkMaster=${env.MASTER} -PsparkHome=${env.SPARK_HOME} -x check -x :sparkling-water-py:integTest		
                                 fi		
 		        
                                """
                                echo 'Archiving artifacts after Integration test'
                                archive includes:'**/build/*tests.log,**/*.log, **/out.*, **/*py.out.txt,examples/build/test-results/binary/integTest/*, **/stdout, **/stderr,**/build/**/*log*, py/build/py_*_report.txt,**/build/reports/'
                        } 
                        catch(err){
                                echo 'Archiving artifacts after Integration test after catch'
                                archive includes:'**/build/*tests.log,**/*.log, **/out.*, **/*py.out.txt,examples/build/test-results/binary/integTest/*, **/stdout, **/stderr,**/build/**/*log*, py/build/py_*_report.txt,**/build/reports/'
                        }

                  }
        }
         		
        stage('QA:Integration test- pySparkling'){
                  
               /* dir("unit-test-stash") {		
                          unstash "unit-test-stash"		
                          sh """
                                # Move the unstashed directory outside the stashed one for the environment variables to pick up properly
                                 mv ${env.WORKSPACE}/unit-test-stash/* ${env.WORKSPACE}
                                 rm -r ${env.WORKSPACE}/unit-test-stash

                           """
                 }
               */
               withEnv(["SPARK_HOME=${env.WORKSPACE}/spark-2.1.0-bin-hadoop2.6","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client'","R_LIBS_USER=${env.WORKSPACE}/Rlibrary","HDP_VERSION=${hdpVersion}","driverHadoopVersion=${driverHadoopVersion}","startH2OClusterOnYarn=${startH2OClusterOnYarn}",		
                        "H2O_PYTHON_WHEEL=${env.WORKSPACE}/private/h2o.whl","H2O_EXTENDED_JAR=${env.WORKSPACE}/assembly-h2o/private/"]		
                        ){   
                        try{
                                 sh"""	

                                 #		
                                 # Run pySparkling integration tests on top of YARN		
                                 #		
                                 if [ "$runPySparklingIntegTests" = true -a "$startH2OClusterOnYarn" = true ]; then 		
                                         ${env.WORKSPACE}/gradlew integTestPython -PbackendMode=${backendMode} -PstartH2OClusterOnYarn -PsparklingTestEnv=$sparklingTestEnv -PsparkMaster=${env.MASTER} -PsparkHome=${env.SPARK_HOME} -x check		
                                         # manually create empty test-result/empty.xml file so Publish JUnit test result report does not fail when only pySparkling integration tests parameter has been selected		
                                         mkdir -p py/build/test-result		
                                         touch py/build/test-result/empty.xml		
                                 fi 		
                                 if [ "$runPySparklingIntegTests" = true -a "$startH2OClusterOnYarn" = false ]; then 		
                                         ${env.WORKSPACE}/gradlew integTestPython -PbackendMode=${backendMode} -PsparklingTestEnv=$sparklingTestEnv -PsparkMaster=${env.MASTER} -PsparkHome=${env.SPARK_HOME} -x check		
                                         # manually create empty test-result/empty.xml file so Publish JUnit test result report does not fail when only pySparkling integration tests parameter has been selected		
                                         mkdir -p py/build/test-result		
                                         touch py/build/test-result/empty.xml		
                                 fi 		
                                """	
                                echo 'Archiving artifacts after Integration test- pySparkling'
                                archive includes:'**/build/*tests.log,**/*.log, **/out.*, **/*py.out.txt,examples/build/test-results/binary/integTest/*, **/stdout, **/stderr,**/build/**/*log*, py/build/py_*_report.txt,**/build/reports/'
             
                        }
                        catch(err){
                                 echo 'Archiving artifacts after Integration test after catch'
                                 archive includes:'**/build/*tests.log,**/*.log, **/out.*, **/*py.out.txt,examples/build/test-results/binary/integTest/*, **/stdout, **/stderr,**/build/**/*log*, py/build/py_*_report.txt,**/build/reports/'
                        }
                  }
            } 

        }




