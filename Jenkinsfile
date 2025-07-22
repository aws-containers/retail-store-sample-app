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
                     sh 'mvn clean install' //-P!docker-tests
                    // sh '''
                    //     echo "Starting Maven build"
                    //      mvn clean install -P!docker-tests
                    // '''
                }
            }
        }
    }
}

        // stage('Build & Test Catalog (Go)') {
        //     steps {
        //         dir('src/catalog') {
        //             sh 'go mod tidy'
        //             sh 'go test ./...'    