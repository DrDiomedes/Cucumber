pipeline {
    agent any

    environment {
        MAVEN_HOME = "${WORKSPACE}/apache-maven-3.8.6"
        JAVA_HOME = "${WORKSPACE}/jdk-17"
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Instalar JDK y Maven') {
            steps {
                sh '''
                # Descargar e instalar Maven
                curl -O https://downloads.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz
                tar -xvzf apache-maven-3.8.6-bin.tar.gz

                # Descargar e instalar JDK 17
                curl -L -o jdk-17_linux-x64_bin.tar.gz https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz
                tar -xvzf jdk-17_linux-x64_bin.tar.gz
                '''
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test -Dbrowser=Chrome'
            }
        }
    }
}
