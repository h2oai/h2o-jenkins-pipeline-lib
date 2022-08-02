def call(def jobName) {
    return jenkins.model.Jenkins.instance.getItem(jobName) != null
}
