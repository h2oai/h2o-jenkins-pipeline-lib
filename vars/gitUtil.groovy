import ai.h2o.ci.GitUtils

def call() {
    _git_utils = create(this)
}

def get() {
    return _git_utils
}
