pipeline {
    agent any

    stages {
        
        stage('Build & Test UI' ) {
            steps {
                dir('src/ui') {
                    sh 'mvn clean install'
                    
                }
            }
        }
    }
    stages {
        stage('Build & Test Catalog') {
            steps {
                script {
                    docker.image('golang:1.21').inside {
                        dir('src/catalog') {
                            sh 'go mod tidy'
                            sh 'go test ./...'
                         }
                    }
                 }
            }
         }
     }
}
