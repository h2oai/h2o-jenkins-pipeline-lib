import ai.h2o.ci.Utils
import static ai.h2o.ci.ColorUtils.*

def call(String msg) {
    echo "[ERROR] !!! ${red(msg)} !!!"
    error msg
}

def call(Exception e) {
    call(ex2str(e))
}
