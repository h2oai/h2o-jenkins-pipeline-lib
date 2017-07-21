package ai.h2o.ci

def getShell() {
    new shell()
}

/**
 * Return version suffix based on current branch
 * and environment variable BUILD_ID.
 */
def getCiVersionSuffix(isRelease = false) {
    return isRelease ? "" : "${env.BRANCH_NAME}_${env.BUILD_ID}"
}

def getCommandOutput(cmd, trim = true) {
    def output = getShell().pipe(cmd)
    return trim ? output.trim() : output
}

def gitDescribeAll() {
    return getShell().pipe("git describe --all --long HEAD").trim()
}

def gitDescribe() {
    return getShell().pipe("git describe --always --long HEAD || echo none").trim()
}

def javaVersion() {
    return getShell().pipe("java -version &2>1 || true").trim()
}

def gitBranch() {
    return getShell().pipe("git rev-parse HEAD | git branch -a --contains | grep -v detached | sed -e 's~remotes/origin/~~g' | tr '\\\n' ' '").trim()
}

def gitHeadSha() {
    return getShell().pipe("git rev-parse HEAD").trim()
}

def uname() {
    return getShell().pipe("uname -a").trim()
}

def hostname() {
    return getShell().pipe("hostname").trim()
}

def getDockerLoadCommandForArtifact(String artifact) {
    return "curl ${env.BUILD_URL}/artifact/${artifact} | gunzip -c | docker load"
}

def getDockerLoadCommandForS3Artifact(String artifactId, String version, String file) {
    return """s3cmd --no-progress get "s3://artifacts.h2o.ai/releases/ai/h2o/${artifactId}/${version}/${file}" - 2>/dev/null | gunzip -d -c - | docker load"""
}

def getShellEnv() {
    def conf = [:]
    def env = getShell().pipe("env")
    for (String line : env.split("\r?\n")) {
        String[] kv = line.split('=')
        if (kv.length == 2) {
            conf[kv[0]] = kv[1]
        }
    }
    return conf
}

def date() {
    return getShell().pipe("date").trim()
}

def user() {
    return getShell().pipe("id -u -n").trim()
}

def getPyBuildInfo(boolean isRelease) {
    def suffix = isRelease ? "" : "+${getCiVersionSuffix()}" // Python dev version requires delimiter '+'
    def content = """
    |suffix="${suffix}"
    |build="${env.BUILD_ID}"
    |commit="${gitHeadSha()}"
    |branch="${gitBranch()}"
    |describe="${gitDescribe()}"
    |build_os="${uname()}"
    |build_machine="${hostname()}"
    |build_date="${date()}"
    |build_user="${user()}"
    """.stripMargin()
    return content
}

def buildInfo() {
    def tableUtils = new ai.h2o.ci.TableUtils()
    def data = [
            "Git Describe"    : gitDescribe(),
            "Git Describe All": gitDescribeAll(),
            "Git Branch"      : gitBranch(),
            "Java version"    : javaVersion(),
            "uname -a"        : uname(),
            "Hostname"        : hostname()
    ]

    def table = tableUtils.table2cols(data, [ 0 : 20])
    return table
}

def envInfo() {
    def tableUtils = new ai.h2o.ci.TableUtils()
    def data = getShellEnv()

    def table = tableUtils.table2cols(data, [ 0 : 20])

    return table
}


/**
 * Version is given as X.Y.Z
 * It returns a tuple (X.Y, Z).
 */
@NonCPS
def fragmentVersion(String version) {
    def xyPartRgx = /\d+.\d+/
    def zPartRgx = /\d+.*/
    def versionRgx = /($xyPartRgx).($zPartRgx)/
    def matcher = (version =~ versionRgx)
    return new Tuple(matcher[0][1], matcher[0][2])
}

@NonCPS
static def banner(String text, int width = 80, String pattern = "*") {
    def out = new StringBuffer()
    out << pattern.center(width, pattern) << '\n'
    out << pattern << ColorUtils.green(text.center(width - 2*pattern.length()) << pattern) << '\n'
    out << pattern.center(width, pattern)
    return out.toString()
}

return this
