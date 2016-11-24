// Local environment map
def envMap = [:]

def setCommonOptions(opts) {
    this.commonOptions = opts
}

def call(args) {
    withEnv(toList(envMap)) {
        sh "./gradlew ${commonOptions} ${args}"
    }
}

def setEnv(String k, v) {
    this.envMap[k] = v
}

// This need Script approval
// Please run this script, let it fail and then
// go to Jenkins > Manage Jenkins > In-process Script Approval and
// white list method signatures
@NonCPS
def toList(m) {
    m.collect {k, v -> "${k}=${v}"}
}

