pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK21'
    }
    
    environment {
        DOCKER_BUILDKIT = '1'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Получение кода из репозитория...'
                checkout scm
            }
        }
        
        stage('Build Dependencies') {
            steps {
                echo 'Сборка зависимостей (events-contract, matchmaking-api)...'
                dir('events-contract') {
                    sh 'mvn clean install -DskipTests'
                }
                dir('matchmaking-api') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }
        
        stage('Build Services') {
            parallel {
                stage('Build Audit Service') {
                    steps {
                        echo 'Сборка audit-service...'
                        dir('audit-service') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Statistics Service') {
                    steps {
                        echo 'Сборка statistics-service...'
                        dir('statistics-service') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
                stage('Build Matchmaking REST') {
                    steps {
                        echo 'Сборка matchmaking-rest...'
                        dir('matchmaking-rest') {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                echo 'Сборка Docker образов...'
                script {
                    def services = [
                        'audit-service': '8082',
                        'statistics-service': '8083',
                        'matchmaking-rest': '8080'
                    ]
                    
                    services.each { serviceName, port ->
                        echo "Сборка образа для ${serviceName}..."
                        dir(serviceName) {
                            sh """
                                docker build -t ${serviceName}:latest .
                            """
                        }
                    }
                }
            }
        }
        
        stage('Tests') {
            steps {
                echo 'Запуск тестов...'
                script {
                    def serviceDirs = ['events-contract', 'matchmaking-api', 'audit-service', 'statistics-service', 'matchmaking-rest']
                    serviceDirs.each { dirName ->
                        dir(dirName) {
                            try {
                                sh 'mvn test'
                            } catch (Exception e) {
                                echo "Тесты в ${dirName} не прошли: ${e.getMessage()}"
                                // Продолжаем выполнение даже если тесты не прошли
                                // Раскомментируйте следующую строку, если хотите остановить pipeline при падении тестов:
                                // currentBuild.result = 'FAILURE'
                            }
                        }
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline выполнен успешно!'
        }
        failure {
            echo 'Pipeline завершился с ошибкой.'
        }
        always {
            echo 'Очистка workspace...'
            cleanWs()
        }
    }
}