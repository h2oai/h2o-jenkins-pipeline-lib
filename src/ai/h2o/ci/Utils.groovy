package ai.h2o.ci

def getShell() {
    new shell()
}

/**
 * Return version suffix based on current branch
 * and environment variable BUILD_ID.
 */
def getCiVersionSuffix() {
    return "${env.BRANCH_NAME}_${env.BUILD_ID}"
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
    return getShell().pipe("git rev-parse --abbrev-ref HEAD").trim()
}

def uname() {
    return getShell().pipe("uname -a").trim()
}

def hostname() {
    return getShell().pipe("hostname").trim()
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

def savePyBuildInfo(targetFile) {
    def content = """
    |suffix=+${getCiVersionSuffix()}
    |build=${env.BUILD_ID}
    |commit=${}
    |describe=${gitDescribe()}
    |build_os=${uname()}
    |build_machine=${hostname()}
    """.stripMargin()

    writeFile(targetFile, content)
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
static def banner(String title, int width=40) {
    def out = new StringBuffer()
    out << "=".center(40, '=') << '\n'
    out << title.center(40) << '\n'
    out << "=".center(40, '=') << '\n'
    out.toString()
}

return this
