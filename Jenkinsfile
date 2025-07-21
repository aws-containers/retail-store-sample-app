pipeline{
    agent any

    environment {
        IMAGE_NAME = "ssiraparapu/Retail-store-sample-app"
        TAG = "${params.DOCKER_TAG}"
        KUBE_NAMESPACE = 'Kube_store'
    }
     stages {
       
        stage('Compile') {
            steps {
                sh "mvn compile"
            }
        }
        
        stage('Tests') {
            steps {
                sh "mvn clean test -X -DskipTests=true"
            }
        }
        
        stage('Build') {
            steps {
                sh "mvn package -DskipTests=true"
            }
        }
     }
}
