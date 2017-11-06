import com.cloudbees.groovy.cps.NonCPS

def call(scm, final String credentialsId, final String context, block) {

  def STATE_NAME_PENDING = 'pending'
  def STATE_NAME_SUCCESS = 'success'
  def STATE_NAME_FAILURE = 'failure'
  def STATE_NAME_ERROR = 'error'

  def STATES = [
    [state: STATE_NAME_PENDING, description: 'The build is pending'],
    [state: STATE_NAME_SUCCESS, description: 'This commit looks good'],
    [state: STATE_NAME_FAILURE, description: 'An error or test failure occurred'],
    [state: STATE_NAME_ERROR, description: 'This commit cannot be built']
  ]

  def PENDING_STATE = STATES.find{it['state'] == STATE_NAME_PENDING}
  def SUCCESS_STATE = STATES.find{it['state'] == STATE_NAME_SUCCESS}
  def FAILURE_STATE = STATES.find{it['state'] == STATE_NAME_FAILURE}

  def currentState = FAILURE_STATE
  try {
    setCommitState(scm, credentialsId, PENDING_STATE['state'], PENDING_STATE['description'], context)
    block()
    currentState = SUCCESS_STATE
  } finally {
    setCommitState(scm, credentialsId, currentState['state'], currentState['description'], context)
  }
}

def setCommitState(scm, final String credentialsId, final String state, final String description, final String context) {
  node('master') {
    withCredentials([string(credentialsId: credentialsId, variable: 'GITHUB_TOKEN')]) {

      def repoName = scm.getUserRemoteConfigs().get(0).getUrl().tokenize('/')[3].split("\\.")[0]

      def relevantHEADRef = scm.getUserRemoteConfigs().get(0).getRefspec().split(':')[0].replaceAll('\\+', '')
      def commitSHAresponse = sh(script: "curl -XGET -v -H \"Authorization: token ${GITHUB_TOKEN}\" https://api.github.com/repos/h2oai/${repoName}/git/${relevantHEADRef}", returnStdout: true).trim()
      def commitSHA = new groovy.json.JsonSlurper().parseText(commitSHAresponse).object.sha

      def params = """
        {
          \\"state\\": \\"${state}\\",
          \\"target_url\\": \\"${BUILD_URL}\\",
          \\"description\\": \\"${description}\\",
          \\"context\\": \\"${context}\\"
        }
      """
      def url = "https://api.github.com/repos/h2oai/${repoName}/statuses/${commitSHA}"
      sh "curl -XPOST -v -H \"Authorization: token ${GITHUB_TOKEN}\" ${url} -d \"${params}\""
    }
  }
}

return this
