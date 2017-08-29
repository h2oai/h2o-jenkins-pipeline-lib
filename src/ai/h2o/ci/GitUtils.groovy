package ai.h2o.ci

class GitUtils implements Serializable {

    def script

    public GitUtils(script) {
        this.script = script
    }

    def pipe(command) {
        return this.script.sh(script: command, returnStdout: true)
    }

    def gitDescribeAll() {
        return pipe("git describe --all --long HEAD").trim()
    }

    def gitDescribe() {
        return pipe("git describe --always --long HEAD || echo none").trim()
    }

    def javaVersion() {
        return pipe("java -version &2>1 || true").trim()
    }

    def gitBranch() {
        return pipe("git rev-parse HEAD | git branch -a --contains | grep -v detached | sed -e 's~remotes/origin/~~g' | tr '\\\n' ' '").trim()
    }

    def gitHeadSha() {
        return pipe("git rev-parse HEAD || echo 'none'").trim()
    }

    def isGitRepo() {
        return pipe("git rev-parse --is-inside-work-tree || echo false").trim() == "true"
    }

    /**
     * Get a change files as they are reported by Jenkins
     * @return list of modified files
     */
    def changeSet() {
        return script.currentBuild.changeSets*.items*.affectedFiles*.path.flatten()
    }
}

