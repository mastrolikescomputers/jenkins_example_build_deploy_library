#!/usr/bin/env groovy

def call(Map config) {
    if (config == null || config.isEmpty()) {
        error "Config map is null or empty"
    }
    if (!config.containsKey('ansibleDir')) {
        error "ansibleDir not found in config"
    }
    if (!config.containsKey('playbook')) {
        error "playbook not found in config"
    }
    if (!config.containsKey('inventory')) {
        error "inventory not found in config"
    }
    if (!config.containsKey('credentialsId')) {
        error "credentialsId not found in config"
    }
    stage('Setup Ansible Environment') {

            dir(config.ansibleDir) {
                echo "Would run: python3 -m venv venv"
                echo "Would run: source venv/bin/activate && pip install --upgrade pip"
                echo "Would run: source venv/bin/activate && pip install ansible boto3 pywinrm"
                echo "Would run: source venv/bin/activate && ansible-galaxy collection install -r requirements.yml -p ./collections"
            }

    }

    stage('Run Ansible Playbook') {

            //withCredentials([sshUserPrivateKey(credentialsId: config.credentialsId,
            //                                   keyFileVariable: 'SSH_KEY_FILE',
            //                                   usernameVariable: 'SSH_USERNAME')]) {
                dir(config.ansibleDir) {
                    def extraVarsString = config.extraVars.collect { k, v -> "-e \"$k=$v\"" }.join(' ')
                    echo "Would run: source venv/bin/activate && ansible-playbook -i ${config.inventory} ${config.playbook} --private-key=\$SSH_KEY_FILE --user=\$SSH_USERNAME ${extraVarsString}"
                }
            //}
    }
}
