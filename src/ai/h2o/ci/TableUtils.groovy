package ai.h2o.ci

@Grab('de.vandermeer:asciitable:0.3.2')
import de.vandermeer.asciitable.AsciiTable

@NonCPS
def table(cols, colSizes, data) {
    def utils = new Utils()
    def at = new AsciiTable()
    at.addRule()
    at.addRow("Git Describe", utils.gitDescribe())
    at.addRow("Git Describe All", utils.gitDescribeAll())
    at.addRule()
    at.render()
}

return this
