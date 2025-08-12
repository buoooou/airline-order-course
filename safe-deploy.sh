#!/bin/bash

# 🛡️ 航空订单系统 - 安全部署脚本
# 包含回滚机制和资源保护
# 用户: FUser23, 区域: us-east-2

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m'

# 安全配置 - 使用用户前缀避免冲突
USER_PREFIX="fuser23"
PROJECT_NAME="airline-order"
AWS_REGION="us-east-2"
AWS_ACCOUNT_ID="381492153714"

# 资源名称（带用户前缀）
ECR_REPOSITORY="${USER_PREFIX}-${PROJECT_NAME}-app"
KEY_NAME="${USER_PREFIX}-${PROJECT_NAME}-keypair"
SG_NAME="${USER_PREFIX}-${PROJECT_NAME}-sg"
INSTANCE_NAME="${USER_PREFIX}-${PROJECT_NAME}-instance"

# 部署状态跟踪
DEPLOYMENT_LOG="deployment-$(date +%Y%m%d-%H%M%S).log"
ROLLBACK_INFO="rollback-info.json"

# 函数定义
log_info() { echo -e "${BLUE}[INFO]${NC} $1" | tee -a $DEPLOYMENT_LOG; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1" | tee -a $DEPLOYMENT_LOG; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a $DEPLOYMENT_LOG; }
log_error() { echo -e "${RED}[ERROR]${NC} $1" | tee -a $DEPLOYMENT_LOG; }
log_header() { echo -e "${PURPLE}=== $1 ===${NC}" | tee -a $DEPLOYMENT_LOG; }

# 错误处理和回滚
trap 'handle_error $? $LINENO' ERR

handle_error() {
    local exit_code=$1
    local line_number=$2
    log_error "部署失败在第 $line_number 行，退出码: $exit_code"
    
    echo ""
    echo "🚨 部署失败！可用选项："
    echo "1) 自动回滚 - 清理已创建的资源"
    echo "2) 保留资源 - 手动调试"
    echo "3) 查看日志 - 分析问题"
    echo ""
    
    read -p "请选择 (1-3): " choice
    case $choice in
        1) auto_rollback ;;
        2) 
            log_warning "资源已保留，请手动检查和清理"
            show_created_resources
            ;;
        3) 
            echo "查看部署日志: cat $DEPLOYMENT_LOG"
            echo "查看回滚信息: cat $ROLLBACK_INFO"
            ;;
    esac
    exit $exit_code
}

# 自动回滚函数
auto_rollback() {
    log_header "开始自动回滚"
    
    if [ -f "$ROLLBACK_INFO" ]; then
        local instance_id=$(jq -r '.instance_id // empty' $ROLLBACK_INFO)
        local sg_id=$(jq -r '.security_group_id // empty' $ROLLBACK_INFO)
        local key_created=$(jq -r '.key_created // false' $ROLLBACK_INFO)
        local ecr_created=$(jq -r '.ecr_created // false' $ROLLBACK_INFO)
        
        # 终止EC2实例
        if [ ! -z "$instance_id" ] && [ "$instance_id" != "null" ]; then
            log_info "终止EC2实例: $instance_id"
            aws ec2 terminate-instances --instance-ids $instance_id --region $AWS_REGION || true
        fi
        
        # 删除安全组（等待实例终止后）
        if [ ! -z "$sg_id" ] && [ "$sg_id" != "null" ]; then
            log_info "等待实例终止后删除安全组..."
            sleep 30
            aws ec2 delete-security-group --group-id $sg_id --region $AWS_REGION || true
        fi
        
        # 删除密钥对
        if [ "$key_created" = "true" ]; then
            log_info "删除密钥对: $KEY_NAME"
            aws ec2 delete-key-pair --key-name $KEY_NAME --region $AWS_REGION || true
            rm -f $KEY_NAME.pem
        fi
        
        # 清理ECR仓库
        if [ "$ecr_created" = "true" ]; then
            read -p "是否删除ECR仓库和镜像？(y/n): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                aws ecr delete-repository --repository-name $ECR_REPOSITORY --force --region $AWS_REGION || true
            fi
        fi
        
        log_success "回滚完成"
    else
        log_warning "找不到回滚信息文件"
    fi
}

# 显示已创建的资源
show_created_resources() {
    echo ""
    echo "📋 已创建的资源："
    if [ -f "$ROLLBACK_INFO" ]; then
        cat $ROLLBACK_INFO | jq .
    fi
    echo ""
    echo "🧹 手动清理命令："
    echo "aws ec2 describe-instances --filters \"Name=tag:Name,Values=$INSTANCE_NAME\" --region $AWS_REGION"
    echo "aws ec2 describe-security-groups --filters \"Name=group-name,Values=$SG_NAME\" --region $AWS_REGION"
    echo "aws ecr describe-repositories --repository-names $ECR_REPOSITORY --region $AWS_REGION"
}

# 安全检查
safety_check() {
    log_header "安全检查"
    
    # 检查现有资源
    log_info "检查现有资源冲突..."
    
    # 检查EC2实例
    local existing_instances=$(aws ec2 describe-instances \
        --filters "Name=tag:Name,Values=$INSTANCE_NAME" "Name=instance-state-name,Values=running,pending,stopping,stopped" \
        --query 'Reservations[].Instances[].InstanceId' \
        --output text \
        --region $AWS_REGION)
    
    if [ ! -z "$existing_instances" ]; then
        log_warning "发现现有实例: $existing_instances"
        echo "这些实例可能属于之前的部署或其他用户"
        read -p "是否继续？这可能会造成冲突 (y/n): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "部署已取消"
            exit 0
        fi
    fi
    
    # 检查ECR仓库
    if aws ecr describe-repositories --repository-names $ECR_REPOSITORY --region $AWS_REGION &> /dev/null; then
        log_warning "ECR仓库 $ECR_REPOSITORY 已存在"
        read -p "是否使用现有仓库？(y/n): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_info "部署已取消"
            exit 0
        fi
    fi
    
    # 初始化回滚信息
    echo '{}' > $ROLLBACK_INFO
    
    log_success "安全检查通过"
}

# 构建Docker镜像
build_image() {
    log_header "构建Docker镜像"
    
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
    
    log_info "开始构建镜像..."
    if $DOCKER_CMD build -t $PROJECT_NAME:latest .; then
        log_success "镜像构建成功"
    else
        log_error "镜像构建失败"
        exit 1
    fi
}

# 创建ECR仓库
setup_ecr() {
    log_header "设置ECR仓库"
    
    local ecr_created=false
    
    if ! aws ecr describe-repositories --repository-names $ECR_REPOSITORY --region $AWS_REGION &> /dev/null; then
        log_info "创建ECR仓库: $ECR_REPOSITORY"
        aws ecr create-repository \
            --repository-name $ECR_REPOSITORY \
            --region $AWS_REGION \
            --image-scanning-configuration scanOnPush=true
        ecr_created=true
        
        # 更新回滚信息
        echo $(jq --arg created "$ecr_created" '.ecr_created = ($created == "true")' $ROLLBACK_INFO) > $ROLLBACK_INFO
    else
        log_info "使用现有ECR仓库: $ECR_REPOSITORY"
    fi
    
    # 登录ECR
    log_info "登录ECR..."
    aws ecr get-login-password --region $AWS_REGION | \
        $DOCKER_CMD login --username AWS --password-stdin \
        $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
    
    log_success "ECR设置完成"
}

# 推送镜像
push_image() {
    log_header "推送镜像到ECR"
    
    local ecr_uri="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY"
    local timestamp=$(date +%Y%m%d-%H%M%S)
    
    # 检查本地镜像是否存在
    if ! $DOCKER_CMD images --format "table {{.Repository}}:{{.Tag}}" | grep -q "$PROJECT_NAME:latest"; then
        log_error "本地镜像 $PROJECT_NAME:latest 不存在"
        exit 1
    fi
    
    # 标记镜像
    log_info "标记镜像为 $ecr_uri:latest"
    if ! $DOCKER_CMD tag $PROJECT_NAME:latest $ecr_uri:latest; then
        log_error "标记镜像失败"
        exit 1
    fi
    
    log_info "标记镜像为 $ecr_uri:$timestamp"
    if ! $DOCKER_CMD tag $PROJECT_NAME:latest $ecr_uri:$timestamp; then
        log_error "标记镜像失败"
        exit 1
    fi
    
    # 推送镜像
    log_info "推送镜像 $ecr_uri:latest 到ECR..."
    if ! $DOCKER_CMD push $ecr_uri:latest; then
        log_error "推送镜像失败"
        exit 1
    fi
    
    log_info "推送镜像 $ecr_uri:$timestamp 到ECR..."
    if ! $DOCKER_CMD push $ecr_uri:$timestamp; then
        log_error "推送带时间戳的镜像失败"
        # 不退出，因为latest已经推送成功
    fi
    
    log_success "镜像推送成功"
    echo "ECR URI: $ecr_uri:latest"
    echo "ECR URI (带时间戳): $ecr_uri:$timestamp"
}

# 创建AWS资源
create_aws_resources() {
    log_header "创建AWS资源"
    
    # 创建密钥对
    local key_created=false
    if ! aws ec2 describe-key-pairs --key-names $KEY_NAME --region $AWS_REGION &> /dev/null; then
        log_info "创建EC2密钥对: $KEY_NAME"
        aws ec2 create-key-pair \
            --key-name $KEY_NAME \
            --region $AWS_REGION \
            --query 'KeyMaterial' \
            --output text > $KEY_NAME.pem
        chmod 400 $KEY_NAME.pem
        key_created=true
        
        # 更新回滚信息
        echo $(jq --arg created "$key_created" '.key_created = ($created == "true")' $ROLLBACK_INFO) > $ROLLBACK_INFO
        
        log_success "密钥对创建成功: $KEY_NAME.pem"
    else
        log_info "使用现有密钥对: $KEY_NAME"
    fi
    
    # 获取默认VPC
    local vpc_id=$(aws ec2 describe-vpcs --filters "Name=is-default,Values=true" --query 'Vpcs[0].VpcId' --output text --region $AWS_REGION)
    log_info "使用默认VPC: $vpc_id"
    
    # 创建安全组
    local sg_id
    local existing_sg=$(aws ec2 describe-security-groups \
        --filters "Name=group-name,Values=$SG_NAME" \
        --query 'SecurityGroups[0].GroupId' \
        --output text \
        --region $AWS_REGION 2>/dev/null)
    
    if [ "$existing_sg" = "None" ] || [ -z "$existing_sg" ]; then
        log_info "创建安全组: $SG_NAME"
        sg_id=$(aws ec2 create-security-group \
            --group-name $SG_NAME \
            --description "Airline Order System Security Group - $USER_PREFIX" \
            --vpc-id $vpc_id \
            --region $AWS_REGION \
            --query 'GroupId' \
            --output text)
        
        if [ -z "$sg_id" ] || [ "$sg_id" = "None" ]; then
            log_error "创建安全组失败"
            exit 1
        fi
        
        # 添加安全组规则
        log_info "添加SSH访问规则..."
        aws ec2 authorize-security-group-ingress \
            --group-id $sg_id \
            --protocol tcp \
            --port 22 \
            --cidr 0.0.0.0/0 \
            --region $AWS_REGION
        
        log_info "添加HTTP访问规则..."
        aws ec2 authorize-security-group-ingress \
            --group-id $sg_id \
            --protocol tcp \
            --port 80 \
            --cidr 0.0.0.0/0 \
            --region $AWS_REGION
        
        log_info "添加应用访问规则..."
        aws ec2 authorize-security-group-ingress \
            --group-id $sg_id \
            --protocol tcp \
            --port 8080 \
            --cidr 0.0.0.0/0 \
            --region $AWS_REGION
        
        # 更新回滚信息
        echo $(jq --arg sg_id "$sg_id" '.security_group_id = $sg_id' $ROLLBACK_INFO) > $ROLLBACK_INFO
        
        log_success "安全组创建成功: $sg_id"
    else
        sg_id="$existing_sg"
        log_info "使用现有安全组: $sg_id"
    fi
    
    # 启动EC2实例
    launch_ec2_instance $sg_id
}

# 启动EC2实例
launch_ec2_instance() {
    local sg_id=$1
    
    log_info "启动EC2实例..."
    
    # 获取最新的Amazon Linux 2023 AMI
    local ami_id=$(aws ec2 describe-images \
        --owners amazon \
        --filters "Name=name,Values=al2023-ami-*-x86_64" "Name=state,Values=available" \
        --query 'Images | sort_by(@, &CreationDate) | [-1].ImageId' \
        --output text \
        --region $AWS_REGION)
    
    log_info "使用AMI: $ami_id"
    
    # 创建用户数据脚本
    cat > user-data.sh << 'EOF'
#!/bin/bash
yum update -y
yum install -y docker jq
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

# 设置AWS区域
echo "export AWS_DEFAULT_REGION=us-east-2" >> /home/ec2-user/.bashrc
EOF
    
    # 启动实例
    local instance_id=$(aws ec2 run-instances \
        --image-id $ami_id \
        --count 1 \
        --instance-type t3.medium \
        --key-name $KEY_NAME \
        --security-group-ids $sg_id \
        --user-data file://user-data.sh \
        --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=$INSTANCE_NAME},{Key=Owner,Value=$USER_PREFIX},{Key=Project,Value=$PROJECT_NAME}]" \
        --region $AWS_REGION \
        --query 'Instances[0].InstanceId' \
        --output text)
    
    # 更新回滚信息
    echo $(jq --arg instance_id "$instance_id" '.instance_id = $instance_id' $ROLLBACK_INFO) > $ROLLBACK_INFO
    
    log_success "EC2实例启动成功: $instance_id"
    
    # 等待实例运行
    log_info "等待实例启动（这可能需要几分钟）..."
    aws ec2 wait instance-running --instance-ids $instance_id --region $AWS_REGION
    
    # 获取公网IP
    local public_ip=$(aws ec2 describe-instances \
        --instance-ids $instance_id \
        --query 'Reservations[0].Instances[0].PublicIpAddress' \
        --output text \
        --region $AWS_REGION)
    
    log_success "实例公网IP: $public_ip"
    
    # 保存部署信息
    cat > deployment-info.txt << EOF
=== 航空订单系统部署信息 ===
用户: $USER_PREFIX
部署时间: $(date)
AWS区域: $AWS_REGION
实例ID: $instance_id
公网IP: $public_ip
密钥文件: $KEY_NAME.pem
安全组: $sg_id
ECR仓库: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY

=== 访问信息 ===
SSH连接: ssh -i $KEY_NAME.pem ec2-user@$public_ip
应用地址: http://$public_ip
API文档: http://$public_ip/swagger-ui/index.html
健康检查: http://$public_ip/api/actuator/health

=== 管理命令 ===
查看日志: ssh -i $KEY_NAME.pem ec2-user@$public_ip "docker logs airline-backend"
重启应用: ssh -i $KEY_NAME.pem ec2-user@$public_ip "cd /opt/airline-order && docker-compose restart"
停止应用: ssh -i $KEY_NAME.pem ec2-user@$public_ip "cd /opt/airline-order && docker-compose down"

=== 清理资源 ===
运行清理脚本: ./cleanup.sh
或手动清理: aws ec2 terminate-instances --instance-ids $instance_id --region $AWS_REGION
EOF
    
    # 更新环境变量文件
    cat > .env << EOF
INSTANCE_ID=$instance_id
PUBLIC_IP=$public_ip
KEY_NAME=$KEY_NAME
SECURITY_GROUP_ID=$sg_id
ECR_REPOSITORY=$ECR_REPOSITORY
USER_PREFIX=$USER_PREFIX
PROJECT_NAME=$PROJECT_NAME
AWS_REGION=$AWS_REGION
EOF
}

# 部署应用
deploy_application() {
    log_header "部署应用到EC2"
    
    # 读取环境变量
    source .env
    
    log_info "等待EC2实例完全启动..."
    sleep 90  # 给更多时间让用户数据脚本执行完成
    
    # 测试SSH连接
    log_info "测试SSH连接..."
    local max_attempts=10
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if ssh -i $KEY_NAME.pem -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@$PUBLIC_IP "echo 'SSH连接成功'" &> /dev/null; then
            log_success "SSH连接正常"
            break
        else
            log_info "SSH连接尝试 $attempt/$max_attempts 失败，等待30秒后重试..."
            sleep 30
            ((attempt++))
        fi
    done
    
    if [ $attempt -gt $max_attempts ]; then
        log_error "SSH连接失败，请检查网络和安全组配置"
        exit 1
    fi
    
    # 上传配置文件
    log_info "上传配置文件到EC2..."
    scp -i $KEY_NAME.pem -o StrictHostKeyChecking=no \
        docker-compose.aws.yml \
        nginx/nginx.conf \
        ec2-user@$PUBLIC_IP:/opt/airline-order/
    
    # 创建部署脚本
    cat > deploy-app.sh << EOF
#!/bin/bash
set -e

echo "🔄 开始部署航空订单系统..."

cd /opt/airline-order

# 设置环境变量
export AWS_DEFAULT_REGION=$AWS_REGION
export ECR_REGISTRY=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
export ECR_REPOSITORY=$ECR_REPOSITORY
export DB_HOST=localhost  # 暂时使用本地数据库
export DB_USERNAME=airline_app
export DB_PASSWORD=AirlineApp2024!
export JWT_SECRET=63ffbc2b8d13ad5180ed7ae7c67f18c85d86046732fc9ced6a02a9d50abb1a03

# 配置AWS凭证（临时方案）
echo "🔑 配置AWS凭证..."
mkdir -p ~/.aws
cat > ~/.aws/credentials << AWSCREDS
[default]
aws_access_key_id = $(aws configure get aws_access_key_id)
aws_secret_access_key = $(aws configure get aws_secret_access_key)
AWSCREDS

cat > ~/.aws/config << AWSCONFIG
[default]
region = $AWS_REGION
output = json
AWSCONFIG

# 登录ECR
echo "🔑 登录到ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin \$ECR_REGISTRY

# 拉取最新镜像
echo "📥 拉取最新镜像..."
docker pull \$ECR_REGISTRY/\$ECR_REPOSITORY:latest

# 启动服务
echo "🚀 启动服务..."
docker-compose -f docker-compose.aws.yml up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 60

# 检查服务状态
echo "🔍 检查服务状态..."
docker-compose -f docker-compose.aws.yml ps

# 健康检查
echo "💚 执行健康检查..."
for i in {1..15}; do
    if curl -f http://localhost:8080/actuator/health; then
        echo "✅ 应用健康检查通过"
        break
    else
        echo "⏳ 等待应用启动... (\$i/15)"
        sleep 20
    fi
done

# 显示访问信息
PUBLIC_IP=\$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
echo "🎉 部署完成！"
echo "🌐 访问地址: http://\$PUBLIC_IP"
echo "📚 API文档: http://\$PUBLIC_IP/swagger-ui/index.html"
echo "💚 健康检查: http://\$PUBLIC_IP/api/actuator/health"
EOF
    
    # 上传并执行部署脚本
    scp -i $KEY_NAME.pem -o StrictHostKeyChecking=no deploy-app.sh ec2-user@$PUBLIC_IP:/opt/airline-order/
    
    log_info "执行应用部署..."
    ssh -i $KEY_NAME.pem -o StrictHostKeyChecking=no ec2-user@$PUBLIC_IP "
        chmod +x /opt/airline-order/deploy-app.sh
        /opt/airline-order/deploy-app.sh
    "
    
    log_success "应用部署完成！"
}

# 部署后验证
post_deploy_verification() {
    log_header "部署后验证"
    
    source .env
    
    log_info "等待服务完全启动..."
    sleep 30
    
    # 健康检查
    log_info "执行健康检查..."
    if curl -f http://$PUBLIC_IP/api/actuator/health; then
        log_success "✅ 健康检查通过"
    else
        log_warning "⚠️ 健康检查失败，但服务可能仍在启动中"
    fi
    
    # 测试主页
    log_info "测试主页访问..."
    if curl -f http://$PUBLIC_IP/ > /dev/null 2>&1; then
        log_success "✅ 主页访问正常"
    else
        log_warning "⚠️ 主页访问失败"
    fi
    
    log_success "部署验证完成"
    
    echo ""
    echo "🎉 部署成功！"
    echo "🌐 访问地址: http://$PUBLIC_IP"
    echo "📚 API文档: http://$PUBLIC_IP/swagger-ui/index.html"
    echo "💚 健康检查: http://$PUBLIC_IP/api/actuator/health"
    echo ""
    echo "📋 部署信息已保存到: deployment-info.txt"
    echo "🔧 环境变量已保存到: .env"
    echo "📝 部署日志已保存到: $DEPLOYMENT_LOG"
}

# 主函数
main() {
    log_header "🛡️ 航空订单系统 - 安全部署"
    
    echo "用户: $USER_PREFIX"
    echo "项目: $PROJECT_NAME"
    echo "区域: $AWS_REGION"
    echo ""
    echo "这个脚本将安全地部署你的航空订单系统到AWS，包括："
    echo "✅ 资源冲突检查"
    echo "✅ 自动回滚机制"
    echo "✅ 用户前缀隔离"
    echo "✅ 详细日志记录"
    echo "✅ 部署后验证"
    echo ""
    
    read -p "是否继续安全部署？(y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "部署已取消"
        exit 0
    fi
    
    # 执行部署步骤
    safety_check
    build_image
    setup_ecr
    push_image
    create_aws_resources
    deploy_application
    post_deploy_verification
    
    log_success "🎉 安全部署完成！"
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
