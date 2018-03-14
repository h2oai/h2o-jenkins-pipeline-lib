import static ai.h2o.ci.Utils.ex2str
import static ai.h2o.ci.ColorUtils.*

def call(Exception e) {
    call(ex2str(e))
}

def call(String msg) {
    echo "[ERROR] !!! ${red(msg)} !!!"
    error msg
}

