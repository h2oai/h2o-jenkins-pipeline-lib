import static ai.h2o.ci.Utils.ex2str
import static ai.h2o.ci.ColorUtils.*

def call(String msg, Exception e = null) {
    echo red("[ERROR] !!! ${msg} !!!")
    if (e != null) {
        echo red(ex2str(e))
    }
    error msg
}

