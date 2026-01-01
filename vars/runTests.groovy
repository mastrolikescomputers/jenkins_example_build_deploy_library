#!/usr/bin/env groovy

def call(Map config) {
    if (config == null || config.isEmpty()) {
        error "Config map is null or empty"
    }
    if (!config.containsKey('testRepoUrl')) {
        error "testRepoUrl not found in config"
    }
    if (!config.containsKey('testFramework')) {
        error "testFramework not found in config"
    }
    stage('Clone Test Repository') {
            echo "Would run: git branch: ${config.testRepoBranch}, credentialsId: ${config.credentialsId}, url: ${config.testRepoUrl}"

    }

    stage('Run Tests') {

            script {
                switch (config.testFramework) {
                    case 'pytest':
                        stage('Run Pytest') {
                            def requirementsPath = config.get('requirementsPath', 'requirements.txt')
                            echo "Would run: python3 -m venv venv"
                            echo "Would run: source venv/bin/activate && pip install -r ${requirementsPath}"
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
    
    stage('Publish Test Results') {

            echo "This is a placeholder for publishing test results."
            echo "The 'junit' step would be used here, for example:"
            echo "junit '**/test-results.xml'"
    }
}
