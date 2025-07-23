pipeline {
    agent any
     environment {
    PATH = "/usr/local/go/bin:${env.PATH}"
  }
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
        stage('Check Go') {
            steps {
                sh 'which go && go version'
            }
        }
        stage('Build & Test Catalog') {
            steps {
                dir('src/catalog') {
                    sh 'go mod tidy'
                    sh 'go test ./...'
                    sh 'docker build -t catalog-service .'
                }
            }
         }
    }
}

       
                

  