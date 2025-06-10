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
                git branch: 'main', url: 'https://github.com/DrDiomedes/cucumbes_code.git'
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

        
        stage('Instalar Chrome y ChromeDriver') {
            steps {
                sh '''
                echo "[INFO] Instalando ChromeDriver manualmente..."

                # Seleccionar versión deseada (puedes ajustarla)
                VERSION="114.0.5735.90"

                # Descargar e instalar ChromeDriver
                curl -sS -O https://chromedriver.storage.googleapis.com/${VERSION}/chromedriver_linux64.zip
                unzip chromedriver_linux64.zip
                chmod +x chromedriver
                mv chromedriver /usr/local/bin/

                echo "[INFO] ChromeDriver instalado:"
                chromedriver --version
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
