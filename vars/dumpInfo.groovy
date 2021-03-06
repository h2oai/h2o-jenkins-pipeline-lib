import static ai.h2o.ci.Utils.banner
import static ai.h2o.ci.ColorUtils.*

def call(String title = "${STAGE_NAME}") {
    def utils = new ai.h2o.ci.Utils()
    def buildInfoTbl = utils.buildInfo()
    def envInfoTbl = utils.envInfo()

    def header = banner(title)
    echo """
    |${header}
    |${yellow(' Basic info '.center(40, '*'))}
    |${buildInfoTbl}
    |
    |${yellow(' Environment '.center(40, '*'))}
    |${envInfoTbl} 
    """.stripMargin('|')
}


