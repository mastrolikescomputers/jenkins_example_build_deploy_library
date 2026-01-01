#!/usr/bin/env groovy

def call(Map config) {
    if (config == null || config.isEmpty()) {
        error "Config map is null or empty"
    }
    if (!config.containsKey('buildTool')) {
        error "buildTool not found in config"
    }
    if (!config.containsKey('projectPath')) {
        error "projectPath not found in config"
    }
    stage('Build') {
            dir(config.projectPath) {
                script {
                    switch (config.buildTool) {
                        case 'dotnet':
                            stage('Build .NET Project') {
                                echo "Would run: dotnet build"
                            }
                            break
                        case 'python':
                            stage('Build Python Project') {
                                def requirementsPath = config.get('requirementsPath', 'requirements.txt')
                                echo "Would run: python3 -m venv venv"
                                echo "Would run: source venv/bin/activate && pip install -r ${requirementsPath}"
                                // Add any other build steps for python, e.g., running tests
                            }
                            break
                        case 'msbuild':
                            stage('Build MSBuild Project') {
                                // This assumes MSBuild is available on the agent
                                // You might need to use a specific agent with MSBuild installed
                                // and configure the path to msbuild.exe
                                echo "Would run: msbuild.exe ${config.projectPath}"
                            }
                            break
                        default:
                            error "Unsupported build tool: ${config.buildTool}"
                            break
                    }
                }
            }
    }

    stage('Archive Artifacts') {
            echo "This is a placeholder for archiving build artifacts."
            echo "The 'archiveArtifacts' step would be used here, for example:"
            echo "archiveArtifacts artifacts: '**/bin/Release*/**, *.whl, *.tar.gz', followSymlinks: false"
    }
}
