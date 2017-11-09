import ai.h2o.ci.BuildInfo
import ai.h2o.ci.SimpleVersionSchema
import ai.h2o.ci.VersionSchema

def call(String name, boolean isRelease) {
    _buildInfo = BuildInfo.create(this, name, isRelease)
}

def call(String name) {
    _buildInfo = BuildInfo.create(this, name, SimpleVersionSchema.create(this))
}

def call(String name, VersionSchema vs) {
    _buildInfo = BuildInfo.create(this, name, vs)
}

def get() {
    return _buildInfo
}
