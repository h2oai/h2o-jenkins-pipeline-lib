import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def call(def jobName, def branchName) {
    def job = Jenkins.get().getItem(jobName)
    print job
    if(job){
        try {
            job = job as WorkflowJob
            print job.getBranch(branchName)
        } catch (org.codehaus.groovy.runtime.typehandling.GroovyCastException ex){
            error("The job is not MultibranchJob " + ex)
        }
    }
    return false
}
