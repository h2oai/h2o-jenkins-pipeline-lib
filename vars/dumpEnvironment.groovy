import ai.h2o.ci.Utils
import ai.h2o.ci.TableUtils
import static ai.h2o.ci.ColorUtils.*

def call(String title = 'Environment') {
    def utils = new Utils()
    def tableUtils = new TableUtils()
    def data = [
      "Git Describe"    : utils.gitDescribe()
      "Git Describe All": utils.gitDescribeAll()
      "Git Branch"   : utils.gitBranch()
      "Java version" : utils.javaVersion()
    ]

    def table = tableUtils.table2cols(data)

    echo """
    +===================+
      ${green(title)}
    +===================+
    Git Describe : ${utils.gitDescribeAll()}
    Git Branch   : ${utils.gitBranch()}
    Java version : ${utils.javaVersion()}
    """
    echo "${table}"
}


