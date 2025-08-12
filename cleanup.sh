#!/bin/bash

# 🧹 航空订单系统 - 资源清理脚本
# 安全清理用户 FUser23 的所有相关资源

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置
USER_PREFIX="fuser23"
PROJECT_NAME="airline-order"
AWS_REGION="us-east-2"

# 资源名称
ECR_REPOSITORY="${USER_PREFIX}-${PROJECT_NAME}-app"
KEY_NAME="${USER_PREFIX}-${PROJECT_NAME}-keypair"
SG_NAME="${USER_PREFIX}-${PROJECT_NAME}-sg"
INSTANCE_NAME="${USER_PREFIX}-${PROJECT_NAME}-instance"

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 显示将要清理的资源
show_resources_to_cleanup() {
    echo "🔍 扫描要清理的资源..."
    echo ""
    
    # 检查EC2实例
    local instances=$(aws ec2 describe-instances \
        --filters "Name=tag:Name,Values=$INSTANCE_NAME" "Name=instance-state-name,Values=running,pending,stopping,stopped" \
        --query 'Reservations[].Instances[].[InstanceId,State.Name,PublicIpAddress]' \
        --output table \
        --region $AWS_REGION 2>/dev/null)
    
    if [ ! -z "$instances" ] && [ "$instances" != "None" ]; then
        echo "📱 EC2实例:"
        echo "$instances"
        echo ""
    fi
    
    # 检查安全组
    local sg_info=$(aws ec2 describe-security-groups \
        --filters "Name=group-name,Values=$SG_NAME" \
        --query 'SecurityGroups[].[GroupId,GroupName,Description]' \
        --output table \
        --region $AWS_REGION 2>/dev/null)
    
    if [ ! -z "$sg_info" ] && [ "$sg_info" != "None" ]; then
        echo "🔒 安全组:"
        echo "$sg_info"
        echo ""
    fi
    
    # 检查密钥对
    local key_info=$(aws ec2 describe-key-pairs \
        --key-names $KEY_NAME \
        --query 'KeyPairs[].[KeyName,KeyFingerprint]' \
        --output table \
        --region $AWS_REGION 2>/dev/null)
    
    if [ ! -z "$key_info" ] && [ "$key_info" != "None" ]; then
        echo "🔑 密钥对:"
        echo "$key_info"
        echo ""
    fi
    
    # 检查ECR仓库
    local ecr_info=$(aws ecr describe-repositories \
        --repository-names $ECR_REPOSITORY \
        --query 'repositories[].[repositoryName,repositoryUri,createdAt]' \
        --output table \
        --region $AWS_REGION 2>/dev/null)
    
    if [ ! -z "$ecr_info" ] && [ "$ecr_info" != "None" ]; then
        echo "📦 ECR仓库:"
        echo "$ecr_info"
        echo ""
        
        # 显示镜像
        local images=$(aws ecr list-images \
            --repository-name $ECR_REPOSITORY \
            --query 'imageIds[].[imageTag,imageDigest]' \
            --output table \
            --region $AWS_REGION 2>/dev/null)
        
        if [ ! -z "$images" ] && [ "$images" != "None" ]; then
            echo "🐳 ECR镜像:"
            echo "$images"
            echo ""
        fi
    fi
}

# 清理EC2实例
cleanup_ec2_instances() {
    log_info "清理EC2实例..."
    
    local instance_ids=$(aws ec2 describe-instances \
        --filters "Name=tag:Name,Values=$INSTANCE_NAME" "Name=instance-state-name,Values=running,pending,stopping,stopped" \
        --query 'Reservations[].Instances[].InstanceId' \
        --output text \
        --region $AWS_REGION)
    
    if [ ! -z "$instance_ids" ] && [ "$instance_ids" != "None" ]; then
        echo "找到实例: $instance_ids"
        
        for instance_id in $instance_ids; do
            local state=$(aws ec2 describe-instances \
                --instance-ids $instance_id \
                --query 'Reservations[0].Instances[0].State.Name' \
                --output text \
                --region $AWS_REGION)
            
            log_info "实例 $instance_id 当前状态: $state"
            
            if [ "$state" != "terminated" ]; then
                log_info "终止实例: $instance_id"
                aws ec2 terminate-instances --instance-ids $instance_id --region $AWS_REGION
                
                log_info "等待实例终止..."
                aws ec2 wait instance-terminated --instance-ids $instance_id --region $AWS_REGION
                log_success "实例 $instance_id 已终止"
            else
                log_info "实例 $instance_id 已经终止"
            fi
        done
    else
        log_info "没有找到要清理的EC2实例"
    fi
}

# 清理安全组
cleanup_security_groups() {
    log_info "清理安全组..."
    
    local sg_id=$(aws ec2 describe-security-groups \
        --filters "Name=group-name,Values=$SG_NAME" \
        --query 'SecurityGroups[0].GroupId' \
        --output text \
        --region $AWS_REGION 2>/dev/null)
    
    if [ ! -z "$sg_id" ] && [ "$sg_id" != "None" ] && [ "$sg_id" != "null" ]; then
        log_info "删除安全组: $sg_id"
        
        # 等待一段时间确保实例完全终止
        sleep 30
        
        # 尝试删除安全组
        local max_attempts=5
        local attempt=1
        
        while [ $attempt -le $max_attempts ]; do
            if aws ec2 delete-security-group --group-id $sg_id --region $AWS_REGION 2>/dev/null; then
                log_success "安全组 $sg_id 已删除"
                break
            else
                log_warning "删除安全组失败，尝试 $attempt/$max_attempts，等待30秒后重试..."
                sleep 30
                ((attempt++))
            fi
        done
        
        if [ $attempt -gt $max_attempts ]; then
            log_error "无法删除安全组 $sg_id，可能仍被其他资源使用"
            log_info "请稍后手动删除: aws ec2 delete-security-group --group-id $sg_id --region $AWS_REGION"
        fi
    else
        log_info "没有找到要清理的安全组"
    fi
}

# 清理密钥对
cleanup_key_pairs() {
    log_info "清理密钥对..."
    
    if aws ec2 describe-key-pairs --key-names $KEY_NAME --region $AWS_REGION &>/dev/null; then
        log_info "删除密钥对: $KEY_NAME"
        aws ec2 delete-key-pair --key-name $KEY_NAME --region $AWS_REGION
        
        # 删除本地密钥文件
        if [ -f "$KEY_NAME.pem" ]; then
            rm -f $KEY_NAME.pem
            log_info "删除本地密钥文件: $KEY_NAME.pem"
        fi
        
        log_success "密钥对已删除"
    else
        log_info "没有找到要清理的密钥对"
    fi
}

# 清理ECR仓库
cleanup_ecr_repository() {
    log_info "清理ECR仓库..."
    
    if aws ecr describe-repositories --repository-names $ECR_REPOSITORY --region $AWS_REGION &>/dev/null; then
        echo ""
        echo "⚠️  ECR仓库包含以下镜像:"
        aws ecr list-images --repository-name $ECR_REPOSITORY --region $AWS_REGION --output table 2>/dev/null || true
        echo ""
        
        read -p "是否删除ECR仓库和所有镜像？这个操作不可逆 (y/n): " -n 1 -r
        echo
        
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            log_info "删除ECR仓库: $ECR_REPOSITORY"
            aws ecr delete-repository --repository-name $ECR_REPOSITORY --force --region $AWS_REGION
            log_success "ECR仓库已删除"
        else
            log_info "保留ECR仓库"
            
            read -p "是否清理仓库中的镜像？(y/n): " -n 1 -r
            echo
            
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                log_info "清理ECR镜像..."
                
                # 获取所有镜像ID
                local image_ids=$(aws ecr list-images \
                    --repository-name $ECR_REPOSITORY \
                    --query 'imageIds[?imageTag!=`latest`]' \
                    --output json \
                    --region $AWS_REGION)
                
                if [ "$image_ids" != "[]" ] && [ ! -z "$image_ids" ]; then
                    echo "$image_ids" | aws ecr batch-delete-image \
                        --repository-name $ECR_REPOSITORY \
                        --image-ids file:///dev/stdin \
                        --region $AWS_REGION
                    log_success "旧镜像已清理"
                fi
            fi
        fi
    else
        log_info "没有找到要清理的ECR仓库"
    fi
}

# 清理本地文件
cleanup_local_files() {
    log_info "清理本地文件..."
    
    local files_to_clean=(
        "$KEY_NAME.pem"
        "deployment-info.txt"
        ".env"
        "rollback-info.json"
        "user-data.sh"
        "deploy-app.sh"
        "deployment-*.log"
    )
    
    for file in "${files_to_clean[@]}"; do
        if ls $file 1> /dev/null 2>&1; then
            rm -f $file
            log_info "删除文件: $file"
        fi
    done
    
    log_success "本地文件清理完成"
}

# 显示清理摘要
show_cleanup_summary() {
    echo ""
    echo "📋 清理摘要:"
    echo "✅ EC2实例已终止"
    echo "✅ 安全组已删除"
    echo "✅ 密钥对已删除"
    echo "✅ 本地文件已清理"
    
    if aws ecr describe-repositories --repository-names $ECR_REPOSITORY --region $AWS_REGION &>/dev/null; then
        echo "ℹ️  ECR仓库已保留"
    else
        echo "✅ ECR仓库已删除"
    fi
    
    echo ""
    echo "🎉 清理完成！"
    echo ""
    echo "如果需要重新部署，请运行:"
    echo "  ./safe-deploy.sh"
}

# 主函数
main() {
    echo "🧹 航空订单系统 - 资源清理工具"
    echo "用户: $USER_PREFIX"
    echo "项目: $PROJECT_NAME"
    echo "区域: $AWS_REGION"
    echo ""
    
    # 检查AWS CLI
    if ! command -v aws &> /dev/null; then
        log_error "AWS CLI未安装"
        exit 1
    fi
    
    # 检查AWS凭证
    if ! aws sts get-caller-identity &> /dev/null; then
        log_error "AWS凭证未配置"
        exit 1
    fi
    
    # 显示要清理的资源
    show_resources_to_cleanup
    
    echo "⚠️  这将删除以上所有资源！"
    echo "⚠️  此操作不可逆，请确认这些资源属于你的项目！"
    echo ""
    
    read -p "确认清理这些资源？(输入 'yes' 确认): " confirmation
    
    if [ "$confirmation" != "yes" ]; then
        log_info "清理已取消"
        exit 0
    fi
    
    echo ""
    log_info "开始清理资源..."
    
    # 执行清理步骤
    cleanup_ec2_instances
    cleanup_security_groups
    cleanup_key_pairs
    cleanup_ecr_repository
    cleanup_local_files
    
    # 显示清理摘要
    show_cleanup_summary
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
