import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def call(def jobName, def branchName) {
    def job = Jenkins.get().getItem(jobName)
    
    job = job as WorkflowJob
    print job.getBranch(branchName)
    return true
}
