pipeline {
    agent any
    parameters{
        booleanParam(name: 'CREATE_INFAR', defaultValue: false, description: 'Flag to trigger create infrastructure')
        string(name: 'BRANCH_BUILD', defaultValue: 'staging', description: 'The branch of git')
        string(name: 'BUILD_SERVICES', defaultValue: '', description: 'List of build services')
    }
    stages {
        stage('Remote to k8s cluster') {
            when{
                expression {
                    return Boolean.valueOf(CREATE_INFAR)
                }
            }
            steps {
                sh 'aws eks --region us-east-1 update-kubeconfig --name Capstone-Cluster'
                sh 'kubectl create namespace capstone-namespace'
                sh 'kubectl create secret docker-registry ecr-secret \
                    --docker-server=248155485793.dkr.ecr.us-east-1.amazonaws.com \
                    --docker-username=AWS \
                    --docker-password=$(aws ecr get-login-password) \
                    --namespace=capstone-namespace'
            }
        }
        stage('Deploy Cloud Config Server') {
            when{
                expression {
                    return "${BUILD_SERVICES}".contains("cloud-config-server")
                }
            }
            steps {
                sh 'kubectl get nodes -o wide'
                sh 'kubectl apply -f kubernetes/config-server.yml'
            }
        }
        stage('Deploy Cloud Gateway') {
            when{
                expression {
                    return "${BUILD_SERVICES}".contains("cloud-gateway")
                }
            }
            steps {
                sh 'kubectl apply -f kubernetes/cloud-gateway.yml'
            }
        }
        stage('Deploy Department Service') {
            when{
                expression {
                    return "${BUILD_SERVICES}".contains("department-service")
                }
            }
            steps {
                sh 'kubectl apply -f kubernetes/department-service-deploy.yml'
            }
        }
        stage('Deploy Hystrix Dashboard') {
            when{
                expression {
                    return "${BUILD_SERVICES}".contains("hystrix-dashboard")
                }
            }
            steps {
                sh 'kubectl apply -f kubernetes/hystrix-dashboard.yml'
            }
        }
        stage('Deploy Service Registry') {
            when{
                expression {
                    return "${BUILD_SERVICES}".contains("service-registry")
                }
            }
            steps {
                sh 'kubectl apply -f kubernetes/service-registry.yml'
            }
        }
        stage('Deploy User Service') {
            when{
                expression {
                    return "${BUILD_SERVICES}".contains("user-service")
                }
            }
            steps {
                sh 'kubectl apply -f kubernetes/user-service-deploy.yml'
            }
        }
        stage('Deployment status') {
            steps {
                sh 'kubectl get nodes --namespace=capstone-namespace'
                sh 'kubectl get pods --namespace=capstone-namespace'
            }
        }
    }
    post {
        always {
            deleteDir() /* clean up our workspace */
        }
    }
}