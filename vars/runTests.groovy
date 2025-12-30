#!/usr/bin/env groovy

def call(Map config) {
    pipeline {
        agent any // Or specify a label for an agent with required tools

        parameters {
            string(name: 'TEST_REPO_URL', description: 'Git repository URL for the tests')
            string(name: 'TEST_REPO_BRANCH', defaultValue: 'main', description: 'Branch to clone')
            string(name: 'TEST_FRAMEWORK', defaultValue: 'pytest', description: 'Test framework to use (e.g., pytest, mstest, nunit)')
        }

        stages {
            stage('Configuration') {
                steps {
                    script {
                        config.credentialsId = config.credentialsId ?: 'git-credentials'
                        config.testFramework = params.TEST_FRAMEWORK
                    }
                }
            }

            stage('Clone Test Repository') {
                steps {
                    echo "Would run: git branch: ${params.TEST_REPO_BRANCH}, credentialsId: ${config.credentialsId}, url: ${params.TEST_REPO_URL}"
                }
            }

            stage('Run Tests') {
                steps {
                    script {
                        switch (config.testFramework) {
                            case 'pytest':
                                stage('Run Pytest') {
                                    echo "Would run: python3 -m venv venv"
                                    echo "Would run: source venv/bin/activate && pip install -r requirements.txt"
                                    echo "Would run: source venv/bin/activate && pytest"
                                }
                                break
                            case 'mstest':
                            case 'nunit':
                                stage("Run .NET Tests") {
                                    // Assumes dotnet CLI is installed
                                    echo "Would run: dotnet test"
                                }
                                break
                            default:
                                error "Unsupported test framework: ${config.testFramework}"
                                break
                        }
                    }
                }
            }
            
            stage('Publish Test Results') {
                steps {
                    echo "This is a placeholder for publishing test results."
                    echo "The 'junit' step would be used here, for example:"
                    echo "junit '**/test-results.xml'"
                }
            }
        }
    }
}
