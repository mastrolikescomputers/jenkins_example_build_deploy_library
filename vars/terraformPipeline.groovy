#!/usr/bin/env groovy

def call(Map config) {
    pipeline {
        agent any
        
        parameters {
            string(name: 'TERRAFORM_ACTION', defaultValue: 'plan', description: 'Terraform action to perform (plan, apply, destroy)')
            string(name: 'ENVIRONMENT', defaultValue: 'dev', description: 'Environment to deploy to (e.g., dev, staging, prod)')
            string(name: 'PROJECT_NAME', defaultValue: 'default-project', description: 'Name of the project')
        }

        stages {
            stage('Configuration') {
                steps {
                    script {
                        // Set default config values
                        config.terraformDir = config.terraformDir ?: '.'
                        config.awsCredentialsId = config.awsCredentialsId ?: 'aws-credentials'
                        config.blueGreenEnabled = config.blueGreenEnabled ?: false
                    }
                }
            }

            stage('Terraform Init') {
                steps {
                    withCredentials([string(credentialsId: config.awsCredentialsId, variable: 'AWS_ACCESS_KEY_ID'),
                                     string(credentialsId: config.awsCredentialsId, variable: 'AWS_SECRET_ACCESS_KEY')]) {
                        dir(config.terraformDir) {
                            echo "Would run: terraform init -backend-config=${params.ENVIRONMENT}.tfbackend"
                        }
                    }
                }
            }

            stage('Terraform Plan') {
                steps {
                    withCredentials([string(credentialsId: config.awsCredentialsId, variable: 'AWS_ACCESS_KEY_ID'),
                                     string(credentialsId: config.awsCredentialsId, variable: 'AWS_SECRET_ACCESS_KEY')]) {
                        dir(config.terraformDir) {
                            echo "Would run: terraform plan -var-file=${params.ENVIRONMENT}.tfvars -out=tfplan"
                        }
                    }
                }
            }

            stage('Approval') {
                when {
                    expression { params.TERRAFORM_ACTION == 'apply' || params.TERRAFORM_ACTION == 'destroy' }
                }
                steps {
                    timeout(time: 15, unit: 'MINUTES') {
                        input message: "Approve ${params.TERRAFORM_ACTION} for ${params.PROJECT_NAME} in ${params.ENVIRONMENT}?"
                    }
                }
            }

            stage('Terraform Apply / Destroy') {
                when {
                    expression { params.TERRAFORM_ACTION == 'apply' || params.TERRAFORM_ACTION == 'destroy' }
                }
                steps {
                    withCredentials([string(credentialsId: config.awsCredentialsId, variable: 'AWS_ACCESS_KEY_ID'),
                                     string(credentialsId: config.awsCredentialsId, variable: 'AWS_SECRET_ACCESS_KEY')]) {
                        dir(config.terraformDir) {
                            if (params.TERRAFORM_ACTION == 'apply') {
                                echo "Would run: terraform apply -auto-approve tfplan"
                            } else if (params.TERRAFORM_ACTION == 'destroy') {
                                echo "Would run: terraform destroy -var-file=${params.ENVIRONMENT}.tfvars -auto-approve"
                            }
                        }
                    }
                }
            }

            stage('Terraform Output') {
                steps {
                    dir(config.terraformDir) {
                        script {
                            echo "Terraform outputs would be displayed here."
                        }
                    }
                }
            }
        }
    }
}
