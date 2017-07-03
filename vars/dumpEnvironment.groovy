import static ai.h2o.ci.ColorUtils.*

def call(String title = 'Environment') {
    def tableUtils = new ai.h2o.ci.TableUtils()
    def data = env

    def table = tableUtils.table2cols(data, [ 0 : 20])

    echo "${table}"
}


