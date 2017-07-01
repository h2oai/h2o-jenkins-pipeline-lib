package ai.h2o.ci

def getShell() {
    new shell()
}

/**
 * Return version suffix based on current branch 
 * and environment variable BUILD_ID.
 */
def getVersionSuffix() {
    return "${env.BRANCH}_${env.BUILD_ID}"
}

def gitDescribeAll() {
    return getShell().pipe("git describe --all --long HEAD").trim()
}

def gitDescribe() {
    return getShell().pipe("git describe --always --long HEAD || echo none").trim()
}

def javaVersion() {
    return getShell().pipe("java -version &2>1 || echo not found").trim()
}

def gitBranch() {
    return getShell().pipe("git rev-parse --abbrev-ref HEAD").trim()
}



return this
