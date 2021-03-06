import ai.h2o.ci.Utils
import static ai.h2o.ci.ColorUtils.*

def call(String title = 'Environment') {
    def utils = new Utils()
    def tableUtils = new ai.h2o.ci.TableUtils()
    def data = [
            "Git Describe"    : utils.gitDescribe(),
            "Git Describe All": utils.gitDescribeAll(),
            "Git Branch"      : utils.gitBranch(),
            "Java version"    : utils.javaVersion(),
            "uname -a"        : utils.uname(),
            "Hostname"        : utils.hostname()
    ]

    def table = tableUtils.table2cols(data, [ 0 : 20])
    echo "${table}"
}


