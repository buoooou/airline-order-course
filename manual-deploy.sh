#!/bin/bash

# 🛫 航空订单系统 - 手动AWS部署脚本
# 用户: FUser23, 区域: us-east-2, 账户: 381492153714

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m'

# 配置变量
AWS_REGION="us-east-2"
AWS_ACCOUNT_ID="381492153714"
PROJECT_NAME="airline-order"
ECR_REPOSITORY="airline-order-app"
STACK_NAME="airline-order-stack"

# 函数定义
log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_header() { echo -e "${PURPLE}=== $1 ===${NC}"; }

# 检查前置条件
check_prerequisites() {
    log_header "检查前置条件"
    
    # 检查AWS CLI
    if ! command -v aws &> /dev/null; then
        log_error "AWS CLI未安装"
        exit 1
    fi
    
    # 检查Docker/Podman
    if command -v podman &> /dev/null; then
        DOCKER_CMD="podman"
        log_info "使用 Podman"
    elif command -v docker &> /dev/null; then
        DOCKER_CMD="docker"
        log_info "使用 Docker"
    else
        log_error "Docker 或 Podman 未安装"
        exit 1
    fi
    
    # 检查AWS凭证
    if ! aws sts get-caller-identity &> /dev/null; then
        log_error "AWS凭证未配置"
        exit 1
    fi
    
    log_success "前置条件检查通过"
}

# 构建Docker镜像
build_image() {
    log_header "构建Docker镜像"
    
    log_info "开始构建镜像..."
    if $DOCKER_CMD build -t $PROJECT_NAME:latest .; then
        log_success "镜像构建成功"
    else
        log_error "镜像构建失败"
        exit 1
    fi
}

# 创建ECR仓库
create_ecr_repo() {
    log_header "创建ECR仓库"
    
    if aws ecr describe-repositories --repository-names $ECR_REPOSITORY --region $AWS_REGION &> /dev/null; then
        log_warning "ECR仓库已存在"
    else
        log_info "创建ECR仓库..."
        aws ecr create-repository \
            --repository-name $ECR_REPOSITORY \
            --region $AWS_REGION \
            --image-scanning-configuration scanOnPush=true
        log_success "ECR仓库创建成功"
    fi
    
    # 登录ECR
    log_info "登录ECR..."
    aws ecr get-login-password --region $AWS_REGION | \
        $DOCKER_CMD login --username AWS --password-stdin \
        $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
    
    log_success "ECR登录成功"
}

# 推送镜像到ECR
push_image() {
    log_header "推送镜像到ECR"
    
    ECR_URI="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY"
    
    # 标记镜像
    $DOCKER_CMD tag $PROJECT_NAME:latest $ECR_URI:latest
    $DOCKER_CMD tag $PROJECT_NAME:latest $ECR_URI:$(date +%Y%m%d-%H%M%S)
    
    # 推送镜像
    log_info "推送镜像到ECR..."
    $DOCKER_CMD push $ECR_URI:latest
    $DOCKER_CMD push $ECR_URI:$(date +%Y%m%d-%H%M%S)
    
    log_success "镜像推送成功"
    echo "ECR URI: $ECR_URI:latest"
}

# 部署AWS基础设施
deploy_infrastructure() {
    log_header "部署AWS基础设施"
    
    if [ -f "aws-infrastructure/deploy-infrastructure.sh" ]; then
        log_info "运行基础设施部署脚本..."
        cd aws-infrastructure
        chmod +x deploy-infrastructure.sh
        ./deploy-infrastructure.sh
        cd ..
        log_success "基础设施部署完成"
    else
        log_warning "基础设施脚本不存在，手动创建资源..."
        create_basic_infrastructure
    fi
}

# 创建基本基础设施
create_basic_infrastructure() {
    log_info "创建基本AWS资源..."
    
    # 创建密钥对
    KEY_NAME="$PROJECT_NAME-keypair"
    if ! aws ec2 describe-key-pairs --key-names $KEY_NAME --region $AWS_REGION &> /dev/null; then
        log_info "创建EC2密钥对..."
        aws ec2 create-key-pair \
            --key-name $KEY_NAME \
            --region $AWS_REGION \
            --query 'KeyMaterial' \
            --output text > $KEY_NAME.pem
        chmod 400 $KEY_NAME.pem
        log_success "密钥对创建成功: $KEY_NAME.pem"
    fi
    
    # 获取默认VPC
    VPC_ID=$(aws ec2 describe-vpcs --filters "Name=is-default,Values=true" --query 'Vpcs[0].VpcId' --output text --region $AWS_REGION)
    log_info "使用默认VPC: $VPC_ID"
    
    # 创建安全组
    SG_NAME="$PROJECT_NAME-sg"
    if ! aws ec2 describe-security-groups --filters "Name=group-name,Values=$SG_NAME" --region $AWS_REGION &> /dev/null; then
        log_info "创建安全组..."
        SG_ID=$(aws ec2 create-security-group \
            --group-name $SG_NAME \
            --description "航空订单系统安全组" \
            --vpc-id $VPC_ID \
            --region $AWS_REGION \
            --query 'GroupId' \
            --output text)
        
        # 添加安全组规则
        aws ec2 authorize-security-group-ingress \
            --group-id $SG_ID \
            --protocol tcp \
            --port 22 \
            --cidr 0.0.0.0/0 \
            --region $AWS_REGION
        
        aws ec2 authorize-security-group-ingress \
            --group-id $SG_ID \
            --protocol tcp \
            --port 80 \
            --cidr 0.0.0.0/0 \
            --region $AWS_REGION
        
        aws ec2 authorize-security-group-ingress \
            --group-id $SG_ID \
            --protocol tcp \
            --port 8080 \
            --cidr 0.0.0.0/0 \
            --region $AWS_REGION
        
        log_success "安全组创建成功: $SG_ID"
    else
        SG_ID=$(aws ec2 describe-security-groups --filters "Name=group-name,Values=$SG_NAME" --query 'SecurityGroups[0].GroupId' --output text --region $AWS_REGION)
        log_info "使用现有安全组: $SG_ID"
    fi
    
    # 启动EC2实例
    launch_ec2_instance $SG_ID $KEY_NAME
}

# 启动EC2实例
launch_ec2_instance() {
    local sg_id=$1
    local key_name=$2
    
    log_info "启动EC2实例..."
    
    # 获取最新的Amazon Linux 2023 AMI
    AMI_ID=$(aws ec2 describe-images \
        --owners amazon \
        --filters "Name=name,Values=al2023-ami-*-x86_64" "Name=state,Values=available" \
        --query 'Images | sort_by(@, &CreationDate) | [-1].ImageId' \
        --output text \
        --region $AWS_REGION)
    
    log_info "使用AMI: $AMI_ID"
    
    # 创建用户数据脚本
    cat > user-data.sh << 'EOF'
#!/bin/bash
yum update -y
yum install -y docker
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# 安装Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 安装AWS CLI v2
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install

# 创建应用目录
mkdir -p /opt/airline-order
chown ec2-user:ec2-user /opt/airline-order
EOF
    
    # 启动实例
    INSTANCE_ID=$(aws ec2 run-instances \
        --image-id $AMI_ID \
        --count 1 \
        --instance-type t3.micro \
        --key-name $key_name \
        --security-group-ids $sg_id \
        --user-data file://user-data.sh \
        --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=$PROJECT_NAME-instance}]" \
        --region $AWS_REGION \
        --query 'Instances[0].InstanceId' \
        --output text)
    
    log_success "EC2实例启动成功: $INSTANCE_ID"
    
    # 等待实例运行
    log_info "等待实例启动..."
    aws ec2 wait instance-running --instance-ids $INSTANCE_ID --region $AWS_REGION
    
    # 获取公网IP
    PUBLIC_IP=$(aws ec2 describe-instances \
        --instance-ids $INSTANCE_ID \
        --query 'Reservations[0].Instances[0].PublicIpAddress' \
        --output text \
        --region $AWS_REGION)
    
    log_success "实例公网IP: $PUBLIC_IP"
    
    # 保存部署信息
    cat > deployment-info.txt << EOF
=== 航空订单系统部署信息 ===
部署时间: $(date)
AWS区域: $AWS_REGION
实例ID: $INSTANCE_ID
公网IP: $PUBLIC_IP
密钥文件: $key_name.pem
安全组: $sg_id
ECR仓库: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY

=== 访问信息 ===
SSH连接: ssh -i $key_name.pem ec2-user@$PUBLIC_IP
应用地址: http://$PUBLIC_IP
API文档: http://$PUBLIC_IP/swagger-ui/index.html
健康检查: http://$PUBLIC_IP/api/actuator/health

=== 下一步 ===
1. 等待实例完全启动（约3-5分钟）
2. 运行部署应用脚本
3. 配置数据库连接
EOF
    
    echo "INSTANCE_ID=$INSTANCE_ID" > .env
    echo "PUBLIC_IP=$PUBLIC_IP" >> .env
    echo "KEY_NAME=$key_name" >> .env
    echo "SECURITY_GROUP_ID=$sg_id" >> .env
}

# 部署应用到EC2
deploy_application() {
    log_header "部署应用到EC2"
    
    # 读取环境变量
    if [ -f ".env" ]; then
        source .env
    else
        log_error "找不到 .env 文件，请先运行基础设施部署"
        exit 1
    fi
    
    log_info "等待EC2实例完全启动..."
    sleep 60
    
    # 创建部署脚本
    cat > deploy-app.sh << 'EOF'
#!/bin/bash
set -e

echo "🔄 开始部署航空订单系统..."

# 配置AWS区域
export AWS_DEFAULT_REGION=us-east-2

# 创建应用目录
sudo mkdir -p /opt/airline-order
sudo chown ec2-user:ec2-user /opt/airline-order
cd /opt/airline-order

# 登录ECR
echo "🔑 登录到ECR..."
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin $ECR_REGISTRY

# 拉取最新镜像
echo "📥 拉取最新镜像..."
docker pull $ECR_REGISTRY/$ECR_REPOSITORY:latest

# 创建环境变量文件
cat > .env << 'ENV_EOF'
ECR_REGISTRY=$ECR_REGISTRY
ECR_REPOSITORY=$ECR_REPOSITORY
DB_HOST=${DB_HOST:-localhost}
DB_USERNAME=${DB_USERNAME:-airline_app}
DB_PASSWORD=${DB_PASSWORD:-AirlineApp2024!}
JWT_SECRET=${JWT_SECRET:-63ffbc2b8d13ad5180ed7ae7c67f18c85d86046732fc9ced6a02a9d50abb1a03}
ENV_EOF

# 停止现有服务
echo "⏹️ 停止现有服务..."
docker-compose -f docker-compose.aws.yml down || true

# 启动服务
echo "🚀 启动服务..."
docker-compose -f docker-compose.aws.yml up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "🔍 检查服务状态..."
docker-compose -f docker-compose.aws.yml ps

# 健康检查
echo "💚 执行健康检查..."
for i in {1..10}; do
    if curl -f http://localhost:8080/actuator/health; then
        echo "✅ 应用健康检查通过"
        break
    else
        echo "⏳ 等待应用启动... ($i/10)"
        sleep 10
    fi
done

# 显示访问信息
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
echo "🎉 部署完成！"
echo "🌐 访问地址: http://$PUBLIC_IP"
echo "📚 API文档: http://$PUBLIC_IP/swagger-ui/index.html"
echo "💚 健康检查: http://$PUBLIC_IP/api/actuator/health"
EOF
    
    # 上传文件到EC2
    log_info "上传配置文件到EC2..."
    scp -i $KEY_NAME.pem -o StrictHostKeyChecking=no \
        docker-compose.aws.yml \
        nginx/nginx.conf \
        deploy-app.sh \
        ec2-user@$PUBLIC_IP:/opt/airline-order/
    
    # 执行部署
    log_info "执行应用部署..."
    ssh -i $KEY_NAME.pem -o StrictHostKeyChecking=no ec2-user@$PUBLIC_IP "
        chmod +x /opt/airline-order/deploy-app.sh
        ECR_REGISTRY=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com \
        ECR_REPOSITORY=$ECR_REPOSITORY \
        /opt/airline-order/deploy-app.sh
    "
    
    log_success "应用部署完成！"
    echo ""
    echo "🌐 访问地址: http://$PUBLIC_IP"
    echo "📚 API文档: http://$PUBLIC_IP/swagger-ui/index.html"
    echo "💚 健康检查: http://$PUBLIC_IP/api/actuator/health"
}

# 主函数
main() {
    log_header "🛫 航空订单系统 - 手动AWS部署"
    
    echo "这个脚本将："
    echo "1. 构建Docker镜像"
    echo "2. 创建ECR仓库并推送镜像"
    echo "3. 创建AWS基础设施（EC2、安全组等）"
    echo "4. 部署应用到EC2"
    echo ""
    
    read -p "是否继续？(y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "部署已取消"
        exit 0
    fi
    
    # 执行部署步骤
    check_prerequisites
    build_image
    create_ecr_repo
    push_image
    deploy_infrastructure
    deploy_application
    
    log_success "🎉 部署完成！"
    echo ""
    echo "📋 部署信息已保存到 deployment-info.txt"
    echo "🔧 环境变量已保存到 .env"
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
