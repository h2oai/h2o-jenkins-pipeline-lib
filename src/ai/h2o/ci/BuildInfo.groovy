package ai.h2o.ci

import com.cloudbees.groovy.cps.NonCPS

class BuildInfo implements Serializable {

    final private String name

    final private boolean isRelease

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
        def bi = new BuildInfo(name, isRelease, gitUtils.gitHeadSha(), gitUtils.changeSet(), gitUtils.changeAuthors(), gitUtils.changeAuthorNames())
        return bi
    }

    private BuildInfo(String name, boolean isRelease, String gitSha, ArrayList<String> changedFiles, ArrayList<String> authorEmails, authorNames) {
        this.name = name
        this.isRelease = isRelease
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

    def getNonLocalVersion() {
        return nonLocalVersion(version)
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

    /**
     * Version is given as X.Y.Z+local_version
     * It returns the X.Y.Z part
     */
    @NonCPS
    def nonLocalVersion(String version) {
        def xyzPartRgx = /\d+.\d+.\d+/
        def localPartRgx = /*/
        def versionRgx = /($xyzPartRgx)+($localPartRgx)/
        def matcher = (version =~ versionRgx)
        return matcher[0][1]
    }

    @Override
    public String toString() {
        return """|name='${name}'
               |version='${version}'
               |isRelease=${isRelease}
               |gitSha=${gitSha}
               """.stripMargin('|')
    }
}




