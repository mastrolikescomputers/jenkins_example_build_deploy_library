//
// EXAMPLE JENKINSFILE FOR PORTFOLIO
//
// This Jenkinsfile demonstrates how to use the shared library.
// The paths and parameters used here are examples and should be
// adapted to a real project's structure and needs.
//

@Library('jenkins-scripts@main') _

pipeline {
    agent any

    parameters {
        string(name: 'ACTION', defaultValue: 'all', description: 'Action to perform (build, test, deploy, all)')
    }

    stages {
        stage('Build') {
            when { expression { params.ACTION == 'build' || params.ACTION == 'all' } }
            steps {
                script {
                    buildPipeline(buildTool: 'dotnet', projectPath: 'my-dotnet-app/')
                }
            }
        }

        stage('Test') {
            when { expression { params.ACTION == 'test' || params.ACTION == 'all' } }
            steps {
                script {
                    runTests(testRepoUrl: 'https://github.com/my-org/my-tests.git', testFramework: 'pytest')
                }
            }
        }

        stage('Deploy Infrastructure') {
            when { expression { params.ACTION == 'deploy' || params.ACTION == 'all' } }
            steps {
                script {
                    terraformPipeline(
                        terraformDir: 'infra/aws',
                        environment: 'staging',
                        action: 'apply'
                    )
                }
            }
        }

        stage('Deploy Application') {
            when { expression { params.ACTION == 'deploy' || params.ACTION == 'all' } }
            steps {
                script {
                    ansiblePipeline(
                        ansibleDir: 'ansible-playbooks',
                        playbook: 'deploy-app.yml',
                        inventory: 'hosts.staging'
                    )
                }
            }
        }
    }
}
