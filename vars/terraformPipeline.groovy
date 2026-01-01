def call(Map config) {
    if (config == null || config.isEmpty()) {
        error "Config map is null or empty"
    }
    if (!config.containsKey('terraformDir')) {
        error "terraformDir not found in config"
    }
    if (!config.containsKey('environment')) {
        error "environment not found in config"
    }
    if (!config.containsKey('action')) {
        error "action not found in config"
    }
    if (!config.containsKey('awsCredentialsId')) {
        error "awsCredentialsId not found in config"
    }
    stage('Terraform Init') {

            //withCredentials([string(credentialsId: config.awsCredentialsId, variable: 'AWS_ACCESS_KEY_ID'),
            //                 string(credentialsId: config.awsCredentialsId, variable: 'AWS_SECRET_ACCESS_KEY')]) {
                dir(config.terraformDir) {
                    echo "Would run: terraform init -backend-config=${config.environment}.tfbackend"
                }
            //}
    }

    stage('Terraform Plan') {

            //withCredentials([string(credentialsId: config.awsCredentialsId, variable: 'AWS_ACCESS_KEY_ID'),
            //                 string(credentialsId: config.awsCredentialsId, variable: 'AWS_SECRET_ACCESS_KEY')]) {
                dir(config.terraformDir) {
                    echo "Would run: terraform plan -var-file=${config.environment}.tfvars -out=tfplan"
                }
            //}
    }

    stage('Approval') {
        when {
            expression { config.action == 'apply' || config.action == 'destroy' }
        }
            timeout(time: 15, unit: 'MINUTES') {
                input message: "Approve ${config.action} for ${config.projectName} in ${config.environment}?"
            }
    }

    stage('Terraform Apply / Destroy') {
        when {
            expression { config.action == 'apply' || config.action == 'destroy' }
        }

            //withCredentials([string(credentialsId: config.awsCredentialsId, variable: 'AWS_ACCESS_KEY_ID'),
            //                 string(credentialsId: config.awsCredentialsId, variable: 'AWS_SECRET_ACCESS_KEY')]) {
                dir(config.terraformDir) {
                    if (config.action == 'apply') {
                        echo "Would run: terraform apply -auto-approve tfplan"
                    } else if (config.action == 'destroy') {
                        echo "Would run: terraform destroy -var-file=${config.environment}.tfvars -auto-approve"
                    }
                }
            //}
    }

    stage('Terraform Output') {
            dir(config.terraformDir) {
                script {
                    echo "Terraform outputs would be displayed here."
                }
            }
    }
}
