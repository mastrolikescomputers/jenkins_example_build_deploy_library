@Library('jenkins_example_build_deploy_library@main') _

pipeline {
    agent any

    parameters {
        string(name: 'ACTION', defaultValue: 'all', description: 'Action to perform (build, test, deploy, all)')
    }

    environment {
        CONFIG = """
            {
                "buildTool": "dotnet",
                "projectPath": "my-dotnet-app/",
                "testRepoUrl": "https://github.com/my-org/my-tests.git",
                "testFramework": "pytest",
                "requirementsPath": "requirements.txt",
                "terraformDir": "infra/aws",
                "environment": "staging",
                "action": "apply",
                "ansibleDir": "ansible-playbooks",
                "playbook": "deploy-app.yml",
                "inventory": "hosts.staging",
                "credentialsId": "your-ssh-credentials-id",
                "extraVars": "{\\"app_version\\":\\"1.2.3\\"}",
                "awsCredentialsId": "your-aws-credentials-id"
            }
        """
    }

    stages {
        stage('Build') {
            when { expression { params.ACTION == 'build' || params.ACTION == 'all' } }
            steps {
                script {
                    def config = readJSON text: CONFIG
                    buildPipeline(config)
                }
            }
        }

        stage('Test') {
            when { expression { params.ACTION == 'test' || params.ACTION == 'all' } }
            steps {
                script {
                    def config = readJSON text: CONFIG
                    runTests(config)
                }
            }
        }

        stage('Deploy Infrastructure') {
            when { expression { params.ACTION == 'deploy' || params.ACTION == 'all' } }
            steps {
                script {
                    def config = readJSON text: CONFIG
                    //terraformPipeline(config)
                }
            }
        }

        stage('Deploy Application') {
            when { expression { params.ACTION == 'deploy' || params.ACTION == 'all' } }
            steps {
                script {
                    def config = readJSON text: CONFIG
                    if (config.extraVars) {
                        config.extraVars = readJSON text: config.extraVars
                    }
                    //ansiblePipeline(config)
                }
            }
        }
    }
}
