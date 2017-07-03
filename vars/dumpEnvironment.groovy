import ai.h2o.ci.Utils
import ai.h2o.ci.TableUtils
import static ai.h2o.ci.ColorUtils.*

def call(String title = 'Environment') {
    def utils = new Utils()
    echo """
    +===================+
      ${green(title)}
    +===================+
    Git Describe : ${utils.gitDescribe()}
    Git Describe : ${utils.gitDescribeAll()}
    Git Branch   : ${utils.gitBranch()}
    Java version : ${utils.javaVersion()}
    """
    def tableUtils = new TableUtils()
    echo "${tableUtils.table(1,2,3)}"
}


