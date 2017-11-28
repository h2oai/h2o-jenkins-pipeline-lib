package ai.h2o.ci

class GitUtils implements Serializable {

    def script

    public GitUtils(script) {
        this.script = script
    }

    public static GitUtils create(script) {
	return new GitUtils(script)
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

    def isPrBranch() {
        return (script.env.CHANGE_BRANCH != null && script.env.CHANGE_BRANCH != '') ||
               script.env.BRANCH_NAME.startsWith("PR-")
    }

    /**
     * Get a change files as they are reported by Jenkins
     * @return list of modified files
     */
    def changeSet() {
        return script.currentBuild.changeSets*.items*.affectedFiles*.path.flatten()
    }

    /**
     * Returns commit messages in current build.
     */
    def changeMsgs() {
        return script.currentBuild.changeSets*.items*.msg.flatten()
    }

    /**
     * Returns author emails for commits in current build.
     *
     * @return

     */
    def changeAuthors() {
        return script.currentBuild.changeSets*.items*.committerEmail.flatten().unique()
    }

    def changeAuthorNames() {
        return script.currentBuild.changeSets*.items*.committer.flatten().unique()
    }


    /**
     * Get author emails for any job.
     * 
     * Note: this needs security approval in sandbox settings
     * @param jenkinsInstance for example `jenkins.model.Jenkins.instance`
     * @param jobName
     * @param buildId
     */
    def changeAuthors(jenkinsInstance, String jobName, String buildId) {
        def job = jenkinsInstance.getItemByFullName(jobName)
        def currentBuild = job.getBuild(buildId)
        return currentBuild == null ? [] : currentBuild.changeSets*.items*.committerEmail.flatten()
    }

}

