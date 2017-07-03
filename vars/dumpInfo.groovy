import static ai.h2o.ci.Utils.banner
import static ai.h2o.ci.ColorUtils.*

def call(String title = 'Info') {
    def header = green(banner(title))
    echo "${title}"
    echo "${' Basic info '.center(40, '*')}"
    dumpBuildInfo()
    echo "${' Environment '.center(40, '*')}"
    dumpEnvironment()
}


