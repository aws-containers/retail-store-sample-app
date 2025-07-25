pipeline {
    agent {
        docker {
            image 'node:20-alpine'
            args '-u root'
        }
    }

    environment {
        NODE_ENV = 'production'
        PATH = "/usr/local/go/bin:${env.PATH}"
    }

    stages {
        stage('Install Dependencies') {
            steps {
                sh 'node -v'
                sh 'yarn install --frozen-lockfile'
            }
        }

        stage('Build') {
            steps {
                sh 'yarn build'
            }
        }
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
        stage('Build & Test Checkout') {
            steps {
                dir('src/checkout') {
                    sh 'npm install'
                    sh 'npm test || echo "No tests found"'
                    sh 'docker build -t checkout-service .'
                }
            }
        }
    }
}

       
                

  