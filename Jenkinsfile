pipeline{
    agent any

    environment {
        IMAGE_NAME = "ssiraparapu/Retail-store-sample-app"
        TAG = "${params.DOCKER_TAG}"
        KUBE_NAMESPACE = 'Kube_store'
    }
    stages {
        // stage('Checkout') {
        //     steps {
        //         git 'https://github.com/aws-containers/retail-store-sample-app.git'
        //     }
        // }
        
        stage('Install Dependencies') {
            steps {
                 dir('src/ui') {
                    sh 'npm install'}
            }
        }
    }
}

