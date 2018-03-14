import static ai.h2o.ci.Utils.ex2str
import static ai.h2o.ci.ColorUtils.*

def call(String msg, Exception e = null) {
    echo blue("[WARN] !!! ${msg} !!!")
    if (e != null) {
        echo blue(ex2str(e))
    }
}

