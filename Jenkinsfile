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
        stage('Build & Test Catalog') {
            steps {
                script {
                    docker.image('golang:1.22').inside {
                        dir('src/catalog') {
                            sh 'go version'
                            sh 'go mod tidy'
                            sh 'go test ./...'
                        }
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                dir('src/catalog') {
                    sh 'docker build -t catalog-service .'
                }
            }
        }
    }
}
                

  