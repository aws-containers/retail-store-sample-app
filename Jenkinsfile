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
        stage('Build & Test Orders') {
            steps {
                dir('src/orders') {
                     sh 'mvn clean install'
                     sh 'docker build -t orders-service .'

                }
            }
        }
        stage('Test in Go 1.22') {
            steps {
                docker.image('golang:1.22').inside {
                    sh 'go version'
                    sh 'go mod tidy'
                    sh 'go test ./...'
                }
            }
        }
         stage('Build & Test Catalog') {
            steps {
                dir('src/catalog') {
                    docker.image('golang:1.22').inside {
                    sh 'go version' 
                    sh 'go mod tidy'
                    sh 'go test ./...'
                    sh 'docker build -t catalog-service .'
                    }
                }
            }
         }
    }
}
  