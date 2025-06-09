pipeline {
    agent any

    environment {
        MAVEN_VERSION = '3.8.6'
        JAVA_VERSION = '17'
        MAVEN_DIR = "${WORKSPACE}/apache-maven-${MAVEN_VERSION}"
        JAVA_DIR = "${WORKSPACE}/jdk-${JAVA_VERSION}"
        PATH = "${JAVA_DIR}/bin:${MAVEN_DIR}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'feature/web-frontUrbano', url: 'https://bitbucket.org/mobilityado/fu-aragon.git'
            }
        }

        stage('Instalar JDK y Maven') {
            steps {
                sh '''
                curl -sL https://archive.apache.org/dist/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz -o maven.tar.gz
                tar -xzf maven.tar.gz

                curl -sL https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.10_7.tar.gz -o jdk.tar.gz
                mkdir -p jdk-17 && tar -xzf jdk.tar.gz -C jdk-17 --strip-components=1
                '''
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test -Dbrowser=Chrome'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
