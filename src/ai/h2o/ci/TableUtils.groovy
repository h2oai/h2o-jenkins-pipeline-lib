package ai.h2o.ci

import com.cloudbees.groovy.cps.NonCPS

@NonCPS
def table2cols(data, colSizes = [:], int leftMargin = 4, int maxWidth = 80) {
    def out = new StringBuffer()
    data.each { k, v ->
        out << k.padRight(20)
        out << v
        out << '\n'
    }
    return out.toString()
}

return this
