pipeline {
    agent any
    tools {
      jdk 'jdk-21' // Must match the name you set in Global Tool Config
    }

    environment {
      JAVA_HOME = tool('jdk-21')
      PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Build') {
            steps {
              sh 'java -version'    // Confirm it prints Java 21
              sh 'mvn clean install'
            }
        }

        stage('Build & Test UI' ) {
            steps {
                dir('src/ui') {
                    sh 'mvn clean install'
                    
                }
            }
        }
    }
}
