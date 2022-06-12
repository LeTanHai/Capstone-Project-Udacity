# micro-k8s-demo
# Deployment step
    1. Create namespace via kubectl create command:
        kubectl create namespace capstone-namespace
    2. Create a registry secret within the above namespace that would be used to pull an image from a private ECR repository:
        kubectl create secret docker-registry ecr-secret \
            --docker-server=${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com \
            --docker-username=AWS \
            --docker-password=$(aws ecr get-login-password) \
            --namespace=capstone-namespace