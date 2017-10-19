def call() {
  // Only PR builds have the CHANGE_BRANCH set
  return env.CHANGE_BRANCH != null && env.CHANGE_BRANCH != ''
}
