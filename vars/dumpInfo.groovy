import static ai.h2o.ci.Utils.banner
import static ai.h2o.ci.ColorUtils.*

def call(String title = 'Info') {
    def header = green(banner(title))
    echo "${header}"

    echo "${yellow(' Basic info '.center(40, '*'))}"
    dumpBuildInfo()
    
    echo "${yellow(' Environment '.center(40, '*'))}"
    dumpEnvironment()
}


