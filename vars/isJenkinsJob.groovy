import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def call(def jobName, def branchName) {
    def job = Jenkins.get().getItem(jobName)
    try {
        job = job as WorkflowJob
        print job.getBranch(branchName)
    } except (org.codehaus.groovy.runtime.typehandling.GroovyCastException exc){
        error("The job is not MultibranchJob")
    }
    return false
}
