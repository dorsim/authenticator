def artifactoryServer = Artifactory.server 'Artifactory'
def buildInfo = Artifactory.newBuildInfo()
def artifactRepo = 'libs-release-local'
def projectName = 'authenticator'
def repositoryPath = "${artifactRepo}/${projectName}/"
def version = "1.0.0-${BUILD_NUMBER}"
def outputFileName = "authenticator-${version}.jar"
def artifactChecksum = ''

pipeline {
    agent any
    tools {
        jdk 'jdk8'
    }
    stages {

        stage ('Build') {
            steps {
                echo 'Start compiling the build...'
                bat "gradlew clean build"
            }
        }

        stage ('Upload Artifact') {
            when {
                branch 'master'
            }
            steps {
                script {
                    def uploadSpec = """{
                     "files": [
                      {
                          "pattern": "build/libs/*.jar",
                          "target": "${repositoryPath}"
                        }
                     ]
                    }"""
                    artifactoryServer.upload(uploadSpec, buildInfo)
                }
            }
        }
    }
}