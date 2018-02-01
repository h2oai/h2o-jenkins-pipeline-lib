package ai.h2o.ci

import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def table2cols(data, colSizes = [:], decorators = [:], int leftMargin = 4, int maxWidth = 80) {
    def out = new StringBuffer()
    def nop = { e -> e }
    data.each { k, v ->
        out << decorators.getOrDefault(0, nop)(k.padLeft(leftMargin).padRight(colSizes.getOrDefault(0, 20)))
        out << decorators.getOrDefault(1, nop)(v)
        out << '\n'
    }
    return out.toString()
}

return this
