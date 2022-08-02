import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject

def call(def multiBranchJobName, def branchName) {
    def job = Jenkins.get().getItem(multiBranchJobName)
    if(job){
        try {
            job = job as WorkflowMultiBranchProject
            def branchFound = job.getBranch(branchName) as boolean
            if(branchFound){
                info("Branch ${branchName} exists for job ${multiBranchJobName}")
                return true
            }
            return false
        } catch (org.codehaus.groovy.runtime.typehandling.GroovyCastException ex){
            error("The job is not MultibranchJob " + ex)
        }
    }
    return false
}
