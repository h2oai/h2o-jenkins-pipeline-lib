package ai.h2o.ci

import com.cloudbees.groovy.cps.NonCPS

class BuildInfo implements Serializable {

    final private String name

    final private boolean isRelease

    final private boolean isPr

    final private String gitSha

    final private ArrayList<String> changedFiles

    final private ArrayList<String> authorEmails

    final private ArrayList<String> authorNames

    private String version

    static BuildInfo create(script, String name, VersionSchema vs) {
        def bi = create(script, name, vs.isRelease())
        bi.setVersion(vs.getVersion())
        return bi
    }

    static BuildInfo create(script, String name, boolean isRelease) {
        def gitUtils = new GitUtils(script)
        def isPr = isPrBranch(script)
        def bi = new BuildInfo(name, isRelease, isPr,
                               gitUtils.gitHeadSha(), gitUtils.changeSet(),
                               gitUtils.changeAuthors(), gitUtils.changeAuthorNames())
        return bi
    }

    private BuildInfo(String name, boolean isRelease, boolean isPr,
                      String gitSha, ArrayList<String> changedFiles,
                      ArrayList<String> authorEmails, authorNames) {
        this.name = name
        this.isRelease = isRelease
        this.isPr = isPr
        this.gitSha = gitSha
        this.changedFiles = changedFiles
        this.authorEmails = authorEmails
        this.authorNames = authorNames
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

    def isPr() {
        return isPr
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

    def getAuthorEmails() {
        return authorEmails
    }

    def getAuthorNames() {
        return authorNames
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
        return """|name='${name}'
               |version='${version}'
               |isRelease=${isRelease}
               |gitSha=${gitSha}
               """.stripMargin('|')
    }

    @NonCPS private def isPrBranch(script) {
        return (script.env.CHANGE_BRANCH != null && script.env.CHANGE_BRANCH != '') ||
                script.env.BRANCH_NAME.startsWith("PR-")

    }
}




