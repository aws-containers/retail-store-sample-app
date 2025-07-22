pipeline {
    agent {

        docker {
            image 'golang:1.21'  // Or any Go version you need
            args '-v $HOME/.cache/go-build:/go/pkg/mod' // Optional caching
        }

    }

    stages {
        
        stage('Build & Test UI' ) {
            steps {
                dir('src/ui') {
                    sh 'mvn clean install'
                    
                }
            }
        }
         stage('Build & Test Catalog') {
            steps {
                dir('src/catalog') {
                    sh 'go mod tidy'
                    sh 'go test ./...'
                }
            }
         }
    }
}
