import static ai.h2o.ci.Utils.ex2str
import static ai.h2o.ci.ColorUtils.*

def call(String msg) {
    echo "[WARN] ${blue(msg)}"
}

def call(Exception e) {
    call(ex2str(e))
}
