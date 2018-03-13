def call(Map config) {
  stage('Checkout') {
    node('docker-worker') {
      deleteDir()
      checkout scm
      stash name: 'source'
    }
  }

  stage('Run All Tests') {
    node('docker-worker') {
      deleteDir()
      unstash 'source'
      sh 'scripts/ci.sh'
    }
  }

  if (env.BRANCH_NAME == 'master') {
    milestone label: 'CI Success'

      stage('Push to CI Success') {
        node {
          checkout scm
            sshagent([config.agent_token]) {
              sh "git push ${config.repository_url} HEAD:refs/heads/ci_success"
            }
        }
      }
  }
}
