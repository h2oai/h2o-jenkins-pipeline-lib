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
    return getShell().pipe("git describe --all --long HEAD")
}

def gitDescribe() {
    return getShell().pipe("git describe --long HEAD")
}


def gitBranch() {
    return getShell().pipe("git rev-parse --abbrev-ref HEAD")
}



return this