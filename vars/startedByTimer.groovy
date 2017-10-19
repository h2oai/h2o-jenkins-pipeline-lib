def call() {
  // may fail if the sandbox permissions are missing
  return currentBuild.rawBuild.getAction(CauseAction.class).findCause(hudson.triggers.TimerTrigger.TimerTriggerCause.class) != null
}
