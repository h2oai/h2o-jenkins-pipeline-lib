#!/usr/bin/groovy

node('mr-0xc2'){
        stage 'Preparation'
                
                git url: 'https://github.com/h2oai/sparkling-water.git'
                def SPARK="spark-${sparkVersion}-bin-hadoop2.6"
                def SPARKTGZ="${SPARK}.tgz"
                sh "wget http://d3kbcqa49mib13.cloudfront.net/${SPARK}.tgz"
                echo 'Preparation done'  
                withEnv(["SPARK_HOME=cd ${SPARK};pwd","HADOOP_CONF_DIR=/etc/hadoop/conf","MASTER='yarn-client',R_LIBS_USER=${env.WORKSPACE}/Rlibrary,HDP_VERSION=${hdpVersion}"]){
                        sh"""echo ${env.BRANCH_NAME}
                             echo ${env.SPARK_HOME}
                             echo ${env.HADOOP_CONF_DIR}
                             echo ${env.HDP_VERSION}
                             mkdir -p ${env.WORKSPACE}/Rlibrary
                             cat <<EOF > $SPARK_HOME/conf/spark-defaults.conf
                             spark.driver.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"
                             spark.yarn.am.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"
                             spark.executor.extraJavaOptions -Dhdp.version="${env.HDP_VERSION}"
                                      EOF
                             cat <<EOF > $SPARK_HOME/conf/java-opts
                                -Dhdp.version="${env.HDP_VERSION}"
                                      EOF
                                // Download necessarry citibike-nyc files
                                mkdir -p ${env.WORKSPACE}/examples/bigdata/laptop/citibike-nyc/
                                echo 'Created citibike directory"
                                //for f in 2013-07.csv 2013-08.csv 2013-09.csv 2013-10.csv 2013-11.csv 2013-12.csv 31081_New_York_City__Hourly_2013.csv
                                 //       do
                                //        cp /home/0xdiag/bigdata/laptop//citibike-nyc/$f $WORKSPACE/examples/bigdata/laptop/citibike-nyc/$f
                                //done

                        """
                        
                }

        stage'QA: build & lint'
            echo 'Testing again'
        
}
