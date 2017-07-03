@Grab('de.vandermeer:asciitable:0.3.2')
import ai.h2o.ci.Utils
import de.vandermeer.asciitable.AsciiTable

def call(String title = 'Environment') {
    def utils = new Utils()
    def at = new AsciiTable()
    at.addRule()
    at.addRow("Git Describe", utils.gitDescribe())
    at.addRule()
    def t = at.render()
    echo "$t"


    echo """
    \033[1;33m
    +===================+
      ${title}
    +===================+\033[0m
    Git Describe : ${utils.gitDescribe()}
    Git Describe : ${utils.gitDescribeAll()}
    Git Branch   : ${utils.gitBranch()}
    Java version : ${utils.javaVersion()}
    """
    at = null
}


