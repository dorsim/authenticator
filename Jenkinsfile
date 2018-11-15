def artifactoryServer = Artifactory.server 'Artifactory'
def buildInfo = Artifactory.newBuildInfo()
def artifactRepo = 'libs-release-local'
def groupId = 'm3l'
def projectName = 'authenticator'
def version = "1.0.3"
def repositoryPath = "${artifactRepo}/${groupId}/${projectName}/${version}/"
def outputFileName = "${projectName}-${version}.jar"

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