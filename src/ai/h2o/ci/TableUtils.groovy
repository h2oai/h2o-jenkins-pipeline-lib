@Grab('de.vandermeer:asciitable:0.3.2')
import de.vandermeer.asciitable.AsciiTable

@NonCPS
def table(cols, colSizes, data) {
    def at = new AsciiTable()
    at.addRule()
    at.addRow("Git Describe", "OOO")
    at.addRule()
    at.render()
}

return this