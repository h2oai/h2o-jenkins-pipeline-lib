import ai.h2o.ci.BuildInfo

def call(String name, boolean isRelease) {
    _buildInfo = BuildInfo.create(this, name, isRelease)
}

def get() {
    return _buildInfo
}
