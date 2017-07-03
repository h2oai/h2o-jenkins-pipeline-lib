package ai.h2o.ci

@Grab('de.vandermeer:asciitable:0.3.2')
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.asciitable.*

@NonCPS
def table2cols(data, colSizes = [:], int leftMargin = 4, int maxWidth = 80) {
    def utils = new Utils()
    def at = new AsciiTable()
    at.addRule()
    data.each { k, v ->
      at.addRow(k, v)
    }
    at.addRule()
    at.getContext().setFrameLeftMargin(leftMargin)
    // Generate constraints
    int[] colWidths = [-1, -1] as int[]
    colSizes.each {int idx, int width -> colWidths[idx] = width}
    def cwc = new CWC_LongestWordMin(colWidths)
    at.getRenderer().setCWC(cwc)
    at.render(maxWidth)
}

return this
