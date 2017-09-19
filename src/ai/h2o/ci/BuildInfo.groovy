package ai.h2o.ci

import com.cloudbees.groovy.cps.NonCPS

class BuildInfo implements Serializable {

    final private String name

    final private boolean isRelease

    final private String gitSha

    final private ArrayList<String> changedFiles

    private String version

    public static BuildInfo create(script, String name, boolean isRelease) {
        def gitUtils = new GitUtils(script)
        def bi = new BuildInfo(name, isRelease, gitUtils.gitHeadSha(), gitUtils.changeSet())
        return bi
    }

    private BuildInfo(String name, boolean isRelease, String gitSha, ArrayList<String> changedFiles) {
        this.name = name
        this.isRelease = isRelease
        this.gitSha = gitSha
        this.changedFiles = changedFiles
    }

    def getMajorVersion() {
        return fragmentVersion(version)[0]
    }

    def getBuildVersion() {
        return fragmentVersion(version)[1]
    }

    def setVersion(String version) {
        this.version = version
    }
    
    def isRelease() {
        return isRelease
    }

    def getVersion() {
        return version
    }

    def getGitSha() {
        return gitSha
    }

    def getChangedFiles() {
        return changedFiles
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

    @Override
    public String toString() {
        return """\
               |name='${name}'
               |version='${version}'
               |isRelease=${isRelease}
               |gitSha=${gitSha}
               """.stripMargin('|')
    }
}




