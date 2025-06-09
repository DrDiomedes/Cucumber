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
                echo "[INFO] Instalando Google Chrome..."
                apt-get update -y
                apt-get install -y wget unzip curl gnupg
                wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
                sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list'
                apt-get update
                apt-get install -y google-chrome-stable

                echo "[INFO] Instalando ChromeDriver..."
                CHROME_VERSION=$(google-chrome-stable --version | grep -oP '\\d+\\.\\d+\\.\\d+')
                DRIVER_VERSION=$(curl -s "https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions-with-downloads.json" | jq -r ".channels.Stable.version")
                curl -sS -O https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/${DRIVER_VERSION}/linux64/chromedriver-linux64.zip
                unzip chromedriver-linux64.zip
                chmod +x chromedriver-linux64/chromedriver
                mv chromedriver-linux64/chromedriver /usr/local/bin/chromedriver

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
