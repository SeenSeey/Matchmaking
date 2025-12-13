pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
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
                        echo 'Сборка и тестирование audit-service...'
                        dir('audit-service') {
                            sh 'mvn clean test'
                        }
                    }
                }
                stage('Build Statistics Service') {
                    steps {
                        echo 'Сборка и тестирование statistics-service...'
                        dir('statistics-service') {
                            sh 'mvn clean test'
                        }
                    }
                }
                stage('Build Matchmaking REST') {
                    steps {
                        echo 'Сборка и тестирование matchmaking-rest...'
                        dir('matchmaking-rest') {
                            sh 'mvn clean test'
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