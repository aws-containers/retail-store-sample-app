pipeline {
    agent any

    stages {      
        stage('Build & Test UI') {
            steps {
                dir('src/ui') {
                  sh 'mvn clean install'
                }
            }
        }
    }
}

