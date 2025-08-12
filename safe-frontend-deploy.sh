#!/bin/bash

# 航空订单系统前端安全部署脚本
# 包含回滚机制和安全检查

set -e

# 配置变量
AWS_REGION="us-east-2"
AWS_ACCOUNT_ID="381492153714"
ECR_REPOSITORY="fuser23-airline-order-frontend"
IMAGE_TAG="latest"
FULL_IMAGE_NAME="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${IMAGE_TAG}"
BACKUP_TAG="backup-$(date +%Y%m%d-%H%M%S)"
ROLLBACK_INFO_FILE="frontend-rollback-info.json"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 错误处理函数
handle_error() {
    log_error "部署过程中发生错误，开始回滚..."
    rollback_deployment
    exit 1
}

# 设置错误陷阱
trap 'handle_error' ERR

# 回滚函数
rollback_deployment() {
    log_warning "开始回滚前端部署..."
    
    if [ -f "$ROLLBACK_INFO_FILE" ]; then
        PREVIOUS_IMAGE=$(jq -r '.previous_image' "$ROLLBACK_INFO_FILE")
        
        if [ "$PREVIOUS_IMAGE" != "null" ] && [ "$PREVIOUS_IMAGE" != "" ]; then
            log_info "回滚到镜像: $PREVIOUS_IMAGE"
            
            ssh -i fuser23-airline-order-keypair.pem -o StrictHostKeyChecking=no ec2-user@18.116.240.81 << ENDSSH
                cd /opt/airline-order
                
                # 停止当前容器
                podman stop airline-frontend 2>/dev/null || true
                podman rm airline-frontend 2>/dev/null || true
                
                # 启动回滚版本
                podman run -d \
                    --name airline-frontend \
                    --network airline-network \
                    -p 80:80 \
                    --restart unless-stopped \
                    $PREVIOUS_IMAGE
                
                echo "回滚完成"
ENDSSH
            log_success "回滚完成"
        else
            log_warning "没有找到可回滚的镜像版本"
        fi
    else
        log_warning "没有找到回滚信息文件"
    fi
}

# 健康检查函数
health_check() {
    log_info "执行健康检查..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        log_info "健康检查尝试 $attempt/$max_attempts"
        
        # 检查容器是否运行
        if ssh -i fuser23-airline-order-keypair.pem -o StrictHostKeyChecking=no ec2-user@18.116.240.81 "podman ps | grep airline-frontend | grep -q Up"; then
            log_info "容器运行正常"
            
            # 检查HTTP响应
            if curl -f -s http://18.116.240.81/ > /dev/null; then
                log_success "前端应用健康检查通过"
                return 0
            else
                log_warning "HTTP健康检查失败，等待5秒后重试..."
            fi
        else
            log_warning "容器未正常运行，等待5秒后重试..."
        fi
        
        sleep 5
        ((attempt++))
    done
    
    log_error "健康检查失败"
    return 1
}

# 主部署流程
main() {
    log_info "🚀 开始前端安全部署流程..."
    
    # 步骤1: 预检查
    log_info "📋 执行预检查..."
    
    # 检查必要文件
    if [ ! -f "frontend/package.json" ]; then
        log_error "frontend/package.json 不存在"
        exit 1
    fi
    
    if [ ! -f "frontend.Dockerfile" ]; then
        log_error "frontend.Dockerfile 不存在"
        exit 1
    fi
    
    # 检查AWS CLI
    if ! command -v aws &> /dev/null; then
        log_error "AWS CLI 未安装"
        exit 1
    fi
    
    # 检查Docker
    if ! command -v podman &> /dev/null; then
        log_error "Docker 未安装"
        exit 1
    fi
    
    log_success "预检查通过"
    
    # 步骤2: 备份当前部署信息
    log_info "💾 备份当前部署信息..."
    
    CURRENT_IMAGE=$(ssh -i fuser23-airline-order-keypair.pem -o StrictHostKeyChecking=no ec2-user@18.116.240.81 "podman inspect airline-frontend --format='{{.Config.Image}}' 2>/dev/null || echo 'none'")
    
    cat > "$ROLLBACK_INFO_FILE" << EOF
{
    "deployment_time": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
    "previous_image": "$CURRENT_IMAGE",
    "new_image": "$FULL_IMAGE_NAME",
    "backup_tag": "$BACKUP_TAG"
}
EOF
    
    log_success "备份信息已保存到 $ROLLBACK_INFO_FILE"
    
    # 步骤3: 检查前端环境配置
    log_info "🔍 检查前端环境配置..."
    if [ ! -f "frontend/src/environments/environment.prod.ts" ]; then
        log_warning "创建生产环境配置文件..."
        mkdir -p frontend/src/environments
        cat > frontend/src/environments/environment.prod.ts << 'EOF'
export const environment = {
  production: true,
  apiUrl: 'http://18.116.240.81:8080'
};
EOF
    fi
    
    # 步骤4: 创建ECR仓库（如果不存在）
    log_info "🏗️  检查/创建ECR仓库..."
    aws ecr describe-repositories --repository-names ${ECR_REPOSITORY} --region ${AWS_REGION} 2>/dev/null || \
    aws ecr create-repository --repository-name ${ECR_REPOSITORY} --region ${AWS_REGION}
    
    # 步骤5: 登录ECR
    log_info "🔐 登录ECR..."
    aws ecr get-login-password --region ${AWS_REGION} | podman login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
    
    # 步骤6: 构建前端Docker镜像
    log_info "🔨 构建前端Docker镜像..."
    podman build --platform=linux/amd64 -f frontend.Dockerfile -t ${FULL_IMAGE_NAME} .
    
    # 步骤7: 推送镜像到ECR
    log_info "📤 推送镜像到ECR..."
    podman push ${FULL_IMAGE_NAME}
    
    # 步骤8: 安全部署到EC2
    log_info "🚀 安全部署到EC2服务器..."
    
    ssh -i fuser23-airline-order-keypair.pem -o StrictHostKeyChecking=no ec2-user@18.116.240.81 << 'ENDSSH'
        cd /opt/airline-order
        
        # 登录ECR
        aws ecr get-login-password --region us-east-2 | podman login --username AWS --password-stdin 381492153714.dkr.ecr.us-east-2.amazonaws.com
        
        # 拉取最新镜像
        podman pull 381492153714.dkr.ecr.us-east-2.amazonaws.com/fuser23-airline-order-frontend:latest
        
        # 创建备份容器（如果当前容器存在）
        if podman ps -a | grep -q airline-frontend; then
            echo "创建当前容器的备份..."
            podman stop airline-frontend 2>/dev/null || true
            podman rename airline-frontend airline-frontend-backup-$(date +%Y%m%d-%H%M%S) 2>/dev/null || true
        fi
        
        # 启动新的前端容器
        podman run -d \
            --name airline-frontend \
            --network airline-network \
            -p 80:80 \
            --restart unless-stopped \
            381492153714.dkr.ecr.us-east-2.amazonaws.com/fuser23-airline-order-frontend:latest
ENDSSH
    
    # 步骤9: 健康检查
    if health_check; then
        log_success "🎉 前端部署成功！"
        
        # 清理备份容器（保留最近3个）
        ssh -i fuser23-airline-order-keypair.pem -o StrictHostKeyChecking=no ec2-user@18.116.240.81 << 'ENDSSH'
            echo "清理旧的备份容器..."
            podman ps -a --filter "name=airline-frontend-backup-" --format "{{.Names}}" | sort -r | tail -n +4 | xargs -r podman rm -f
ENDSSH
        
        log_info "📱 访问地址:"
        log_info "  🌐 前端应用: http://18.116.240.81"
        log_info "  🔗 后端API: http://18.116.240.81:8080/api/flights"
        log_info "  📚 API文档: http://18.116.240.81:8080/swagger-ui/index.html"
        
    else
        log_error "健康检查失败，开始回滚..."
        rollback_deployment
        exit 1
    fi
}

# 如果直接运行脚本
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    # 检查参数
    if [[ "$1" == "--rollback" ]]; then
        log_info "🔄 执行手动回滚..."
        rollback_deployment
    else
        main "$@"
    fi
fi
