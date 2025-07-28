pipeline {
    agent any

    environment {
    JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
    PATH = "${JAVA_HOME}/bin:${env.PATH}"
}
    stages {      
        stage('Build & Test UI') {
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
    }
}

