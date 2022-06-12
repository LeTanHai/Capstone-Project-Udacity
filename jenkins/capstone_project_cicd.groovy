pipeline {
    agent any
    parameters {
        booleanParam(name: 'CREATE_INFAR', defaultValue: false, description: 'Flag to trigger create infrastructure')
        choice(
            name: 'BRANCH_BUILD',
            choices: ['staging', 'preproduction', 'production'],
            description: 'Branch build from git'
        )
        checkboxParameter(name: 'BUILD_SERVICES', format: 'JSON',
            pipelineSubmitContent: '{"CheckboxParameter": [{"key": "Cloud Config Server","value": "cloud-config-server"},{"key": "Cloud Gateway","value": "cloud-gateway"},{"key": "Department Service","value": "department-service"},{"key": "Hystrix Dashboard","value": "hystrix-dashboard"},{"key": "Service Registry","value": "service-registry"},{"key": "User Service","value": "user-service"}]}', description: '')
    }
    stages {
        stage('BUILD_SPRING_BOOT_SERVICES') {
            steps {
                build(job: 'BUILD_SPRING_BOOT_SERVICES', parameters: [
                    string(name: 'BRANCH_BUILD', value: String.valueOf(BRANCH_BUILD)),
                    string(name: 'BUILD_SERVICES', value: String.valueOf(BUILD_SERVICES))
                ])
            }
        }

        stage('CREATE_INFRASTRUCTURE') {
            when{
                expression {
                    return Boolean.valueOf(CREATE_INFAR)
                }
            }
            steps {
                sh 'cd cloudformation && aws cloudformation create-stack --stack-name capstoneStack --template-body file://infrastructure.yml  --parameters file://parameters.json --capabilities "CAPABILITY_IAM" "CAPABILITY_NAMED_IAM" --region=us-east-1'
                sh 'aws cloudformation wait stack-create-complete --region us-east-1 --stack-name capstoneStack'
            }
        }

        stage('DEPLOY_SERVERS') {
            steps {
                build(job: 'DEPLOY_SPRING_BOOT_SERVICES', parameters: [
                    booleanParam(name: 'CREATE_INFAR', value: Boolean.valueOf(CREATE_INFAR)),
                    string(name: 'BRANCH_BUILD', value: String.valueOf(BRANCH_BUILD)),
                    string(name: 'BUILD_SERVICES', value: String.valueOf(BUILD_SERVICES))
                ])
            }
        }
    }
    post {
        always {
            deleteDir() /* clean up our workspace */
        }
    }
}