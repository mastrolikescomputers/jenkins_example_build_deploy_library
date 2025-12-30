#!/usr/bin/env groovy

def call(Map config) {
    pipeline {
        agent any

        parameters {
            string(name: 'BUILD_TOOL', defaultValue: 'dotnet', description: 'Build tool to use (dotnet, python, msbuild)')
            string(name: 'PROJECT_PATH', defaultValue: '.', description: 'Path to the project or solution file')
        }

        stages {
            stage('Configuration') {
                steps {
                    script {
                        config.buildTool = params.BUILD_TOOL
                        config.projectPath = params.PROJECT_PATH
                    }
                }
            }

            stage('Build') {
                steps {
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
                                        echo "Would run: python3 -m venv venv"
                                        echo "Would run: source venv/bin/activate && pip install -r requirements.txt"
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
            }

            stage('Archive Artifacts') {
                steps {
                    echo "This is a placeholder for archiving build artifacts."
                    echo "The 'archiveArtifacts' step would be used here, for example:"
                    echo "archiveArtifacts artifacts: '**/bin/Release*/**, *.whl, *.tar.gz', followSymlinks: false"
                }
            }
        }
    }
}
