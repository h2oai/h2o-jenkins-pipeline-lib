import ai.h2o.ci.Utils
import static ai.h2o.ci.ColorUtils.*

def call(String title = 'Environment') {
    def utils = new Utils()
    def tableUtils = new ai.h2o.ci.TableUtils()
    def data = utils.getShellEnv()

    def table = tableUtils.table2cols(data, [ 0 : 20])

    echo "${table}"
}


