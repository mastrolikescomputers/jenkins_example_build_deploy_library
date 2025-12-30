# Generic Jenkins Pipeline Library

## Disclaimer

**This repository is intended as a portfolio piece to demonstrate concepts of Jenkins Shared Libraries.**

The code within this repository is for demonstration purposes only. It is not a production-ready, plug-and-play solution. The pipeline scripts have been intentionally modified to only `echo` the commands that would be run, rather than executing them. This is to prevent accidental execution of powerful commands like `terraform apply` or `ansible-playbook`.

To make this library functional, you would need to:
1.  Replace the `echo "Would run: ..."` statements with actual `sh` or `bat` commands.
2.  Provide real credentials and configurations for your environment.
3.  Adapt the file paths and parameters to your specific projects.

---

This repository contains a collection of generic Jenkins pipeline scripts designed to be used as a shared library.

## Structure

*   `vars/`: This directory contains the main pipeline scripts.
    *   `terraformPipeline.groovy`: For running Terraform deployments.
    *   `ansiblePipeline.groovy`: For running Ansible playbooks.
    *   `buildPipeline.groovy`: For building .NET, Python, and MSBuild projects.
    *   `runTests.groovy`: For running automated tests.
*   `resources/`: This directory can be used for helper scripts or configuration files.
*   `Jenkinsfile`: An example of how to use this shared library in a project.

## Jenkins Configuration

1.  **Add the Shared Library in Jenkins:**
    *   Go to "Manage Jenkins" -> "Configure System".
    *   Under "Global Pipeline Libraries", add a new library.
    *   **Name:** `jenkins-scripts` (or your preferred name).
    *   **Default version:** `main` (or your default branch).
    *   **Source Code Management:** Select "Git" and provide the repository URL.
    *   Save the configuration.

2.  **Use the Library in your Jenkinsfile:**
    *   At the top of your `Jenkinsfile`, add the following line:
        ```groovy
        @Library('jenkins-scripts@main') _
        ```

## Pipeline Usage

### `terraformPipeline`

This pipeline runs Terraform to manage your infrastructure.

**Example:**

```groovy
terraformPipeline(
    terraformDir: 'infra/aws',       // Directory with .tf files
    environment: 'staging',          // .tfvars and .tfbackend file to use
    action: 'apply',                 // 'plan', 'apply', or 'destroy'
    awsCredentialsId: 'aws-creds'    // Jenkins credentials for AWS
)
```

### `ansiblePipeline`

This pipeline runs Ansible playbooks to configure servers or deploy applications.

**Example:**

```groovy
ansiblePipeline(
    ansibleDir: 'ansible',                  // Directory with playbook
    playbook: 'deploy.yml',                 // Playbook to run
    inventory: 'hosts.staging',             // Inventory file
    credentialsId: 'ssh-private-key',       // SSH credentials
    extraVars: [app_version: '1.2.3']       // Extra variables for playbook
)
```

### `buildPipeline`

This pipeline builds projects using various build tools.

**Example:**

```groovy
buildPipeline(
    buildTool: 'dotnet',              // 'dotnet', 'python', or 'msbuild'
    projectPath: 'my-dotnet-app/'     // Path to project/solution
)
```

### `runTests`

This pipeline clones a test repository and runs automated tests.

**Example:**

```groovy
runTests(
    testRepoUrl: 'https://github.com/my-org/my-tests.git',
    testRepoBranch: 'develop',
    credentialsId: 'git-credentials',
    testFramework: 'pytest'            // 'pytest', 'mstest', 'nunit'
)
```
