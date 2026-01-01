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

**Parameters:**

*   `terraformDir`: The directory containing your Terraform files (`.tf`).
*   `environment`: The name of the environment to apply changes to. This will be used to select the appropriate `.tfvars` and `.tfbackend` files (e.g., `staging.tfvars`).
*   `action`: The Terraform action to perform. Can be `plan`, `apply`, or `destroy`.
*   `awsCredentialsId`: The ID of the Jenkins credentials to use for authenticating with AWS. This should be a "String" credential containing your AWS secret key ID.

**Example:**

```groovy
terraformPipeline(
    terraformDir: 'infra/aws',       // Directory with .tf files
    environment: 'staging',          // .tfvars and .tfbackend file to use
    action: 'apply',                 // 'plan', 'apply', or 'destroy'
    awsCredentialsId: 'your-aws-credentials-id'    // Jenkins credentials for AWS
)
```

### `ansiblePipeline`

This pipeline runs Ansible playbooks to configure servers or deploy applications.

**Parameters:**

*   `ansibleDir`: The directory containing your Ansible playbook.
*   `playbook`: The name of the playbook to run.
*   `inventory`: The name of the inventory file to use.
*   `credentialsId`: The ID of the Jenkins SSH credentials to use for connecting to the target servers. This should be a "SSH Username with private key" credential.
*   `extraVars`: A map of extra variables to pass to the playbook.

**Example:**

```groovy
ansiblePipeline(
    ansibleDir: 'ansible',                  // Directory with playbook
    playbook: 'deploy.yml',                 // Playbook to run
    inventory: 'hosts.staging',             // Inventory file
    credentialsId: 'your-ssh-credentials-id', // SSH credentials
    extraVars: [app_version: '1.2.3']       // Extra variables for playbook
)
```

### `buildPipeline`

This pipeline builds projects using various build tools.

**Parameters:**

*   `buildTool`: The build tool to use. Supported values are `dotnet`, `python`, and `msbuild`.
*   `projectPath`: The path to the project or solution file.
*   `requirementsPath`: (Optional) The path to the `requirements.txt` file for Python builds. Defaults to `requirements.txt`.

**Example:**

```groovy
buildPipeline(
    buildTool: 'python',              // 'dotnet', 'python', or 'msbuild'
    projectPath: 'my-python-app/',     // Path to project/solution
    requirementsPath: 'my-python-app/requirements.txt'
)
```

### `runTests`

This pipeline clones a test repository and runs automated tests.

**Parameters:**

*   `testRepoUrl`: The URL of the Git repository containing the tests.
*   `testRepoBranch`: The branch to clone from the test repository.
*   `credentialsId`: The ID of the Jenkins credentials to use for cloning the test repository.
*   `testFramework`: The test framework to use. Supported values are `pytest`, `mstest`, and `nunit`.
*   `requirementsPath`: (Optional) The path to the `requirements.txt` file for Python-based tests. Defaults to `requirements.txt`.

**Example:**

```groovy
runTests(
    testRepoUrl: 'https://github.com/my-org/my-tests.git',
    testRepoBranch: 'develop',
    credentialsId: 'git-credentials',
    testFramework: 'pytest',            // 'pytest', 'mstest', 'nunit'
    requirementsPath: 'tests/requirements.txt'
)
```
