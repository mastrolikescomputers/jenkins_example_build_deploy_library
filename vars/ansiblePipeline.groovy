#!/usr/bin/env groovy

def call(Map config) {
    pipeline {
        agent any

        parameters {
            string(name: 'PLAYBOOK', defaultValue: 'deploy.yml', description: 'Ansible playbook to run')
            string(name: 'INVENTORY', defaultValue: 'hosts', description: 'Ansible inventory file')
            string(name: 'PROJECT_NAME', defaultValue: 'default-project', description: 'Name of the project')
        }

        stages {
            stage('Configuration') {
                steps {
                    script {
                        config.ansibleDir = config.ansibleDir ?: '.'
                        config.credentialsId = config.credentialsId ?: 'ansible-credentials'
                        config.extraVars = config.extraVars ?: [:]
                    }
                }
            }

            stage('Setup Ansible Environment') {
                steps {
                    dir(config.ansibleDir) {
                        echo "Would run: python3 -m venv venv"
                        echo "Would run: source venv/bin/activate && pip install --upgrade pip"
                        echo "Would run: source venv/bin/activate && pip install ansible boto3 pywinrm"
                        echo "Would run: source venv/bin/activate && ansible-galaxy collection install -r requirements.yml -p ./collections"
                    }
                }
            }

            stage('Run Ansible Playbook') {
                steps {
                    withCredentials([sshUserPrivateKey(credentialsId: config.credentialsId,
                                                       keyFileVariable: 'SSH_KEY_FILE',
                                                       usernameVariable: 'SSH_USERNAME')]) {
                        dir(config.ansibleDir) {
                            def extraVarsString = config.extraVars.collect { k, v -> "-e \"$k=$v\"" }.join(' ')
                            echo "Would run: source venv/bin/activate && ansible-playbook -i ${params.INVENTORY} ${params.PLAYBOOK} --private-key=\$SSH_KEY_FILE --user=\$SSH_USERNAME ${extraVarsString}"
                        }
                    }
                }
            }
        }
    }
}
