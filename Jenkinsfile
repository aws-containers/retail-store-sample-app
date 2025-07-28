pipeline {
    agent any
    tools {
      jdk 'jdk-21' // Must match the name you set in Global Tool Config
    }

    environment {
      JAVA_HOME = tool('jdk-21')
      PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
      MAVEN_OPTS = "-Xmx1024m"
    }
    stages {
        stage('Verify Java') {
            steps {
                sh 'java -version'
                sh 'echo $JAVA_HOME'
            }
        }

        stage('Build') {
            steps {
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
