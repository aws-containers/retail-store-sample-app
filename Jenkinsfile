pipeline {
    agent any
    tools {
        jdk 'jdk-21'
    }

    environment {
        JAVA_HOME = tool('jdk-21')
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
        MAVEN_OPTS = "-Xmx1024m"
    }

    stages {
        stage('Verify Java') {
            steps {
                sh '''
                    echo "JAVA_HOME=$JAVA_HOME"
                    java -version
                    mvn -version
                '''
            }
        }

        stage('Build & Test UI') {
            steps {
                dir('src/ui') {
                  sh 'mvn clean install'
                }
            }
        }
    }
}

