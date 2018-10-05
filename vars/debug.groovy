import ai.h2o.ci.Utils
import static ai.h2o.ci.ColorUtils.*

def call(String msg) {
    echo lightGrey("🕷 ===== [DEBUG] ==== ${msg}")
}
