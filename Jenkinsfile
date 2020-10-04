node('') {

    /* Only keep the 10 most recent builds. */
    properties([[$class: 'BuildDiscarderProperty',
                strategy: [$class: 'LogRotator', numToKeepStr: '10']]])

    //def branchName = currentBuild.projectName
    //def buildNumber = currentBuild.number

    def VERSION = 'latest'
    def PROJECT = 'zelen-app'
    //def REG_URL = 'https://hub.docker.com'
    //def REG_CRED = ''

    def git_repo = checkout scm
    def gitCommit = git_repo.GIT_COMMIT
    def gitBranch = git_repo.GIT_BRANCH
    def shortGitCommit = "${gitCommit[0..10]}"
    def previousGitCommit = sh(script: "git rev-parse ${gitCommit}~", returnStdout: true)

    env.DOCKER_PROJECT = 'zelen-app'
    //def dockerfile = 'Dockerfile'

    IMAGE = readMavenPom().getArtifactId()
    VERSION = readMavenPom().getVersion()
    //VERSION = "${VERSION}-${shortGitCommit}"
    echo "IMAGE: ${IMAGE}"
    echo "VERSION: ${VERSION}"

    stage('Build java jar') {
        withMaven(maven: 'Maven 3.6.3',jdk: 'Java 8') {
            sh "mvn clean install"
        }
    }   

    stage("Build docker image") {      
        def dockerImage = docker.build("${PROJECT}:${VERSION}", "--build-arg APP_ARTIFACT=${IMAGE}-${VERSION}.jar .")
    }

    stage("Deploy to docker hphost") {
        docker.image('docker/compose').inside('-v /var/run/docker.sock:/var/run/docker.sock -e "PROJECT=${PROJECT}" -e "VERSION=${VERSION}"') {
            sh 'cat docker-compose.yml | envsubst | docker-compose pull && docker-compose up -d --force-recreate'
        }
    }

}
