def call() {
  def hi = Hudson.instance
  def jobNameParts = env.JOB_NAME.tokenize('/')
  def pname = jobNameParts[0]

  def item = hi.getItem(pname)
  // if called from multibranch pipeline/folder etc., we need to get the builds of the leaf item
  if (jobNameParts.size() > 1) {
    // remove the first element, because that has been already used to get the root item
    jobNameParts.remove(0)
    for (part in jobNameParts) {
      item = item.getItem(part)
    }
  }

  item.getBuilds().each{ build ->
    def exec = build.getExecutor()

    if (build.number != currentBuild.number && exec != null) {
      exec.doStop()
      println("Aborted previous running build #${build.number}")
    } else {
      println("Build is not running or is current build, not aborting - #${build.number}")
    }
  }
}
