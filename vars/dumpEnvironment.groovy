//@Grab('de.vandermeer:asciitable:0.3.2')
import ai.h2o.ci.Utils
import static ai.h2o.ci.ColorUtils.*
//import de.vandermeer.asciitable.AsciiTable

def call(String title = 'Environment') {
    def utils = new Utils()
    /*def at = new AsciiTable()
    at.addRule()
    at.addRow("Git Describe", utils.gitDescribe())
    at.addRule()
    def t = at.render()*/
    echo "$t"

    echo """
    +===================+
      ${green(${title})}
    +===================+
    Git Describe : ${utils.gitDescribe()}
    Git Describe : ${utils.gitDescribeAll()}
    Git Branch   : ${utils.gitBranch()}
    Java version : ${utils.javaVersion()}
    """
}


