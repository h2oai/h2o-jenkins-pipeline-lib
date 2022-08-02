import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject

def call(def multiBranchJobName, def branchName) {
    def job = Jenkins.get().getItem(multiBranchJobName)
    if(job){
        try {
            job = job as WorkflowMultiBranchProject
            print job.getBranch(branchName)
        } catch (org.codehaus.groovy.runtime.typehandling.GroovyCastException ex){
            error("The job is not MultibranchJob " + ex)
        }
    }
    return false
}
