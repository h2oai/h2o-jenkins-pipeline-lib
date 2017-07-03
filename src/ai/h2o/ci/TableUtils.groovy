package ai.h2o.ci

@Grab('de.vandermeer:asciitable:0.3.2')
import de.vandermeer.asciitable.AsciiTable

@NonCPS
def table2cols(data, leftMargin = 4, colSizes = [:]) {
    def utils = new Utils()
    def at = new AsciiTable()
    at.addRule()
    data.each { k, v ->
      at.addRow(k, v)
    }
    at.addRule()
    at.getContext().setFrameLeftMargin(leftMargin)
    at.render()
}

return this
