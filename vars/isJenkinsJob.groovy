import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject

def call(def jobName, def branchName) {
    def job = Jenkins.get().getItem(jobName)
    job = job as org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
    print job.getBranch(branchName)
    return true
}
