# 🛡️ 安全部署指南 - 防止误删和自动回滚

## 🚨 安全考虑

### 为什么需要安全部署？
1. **共享环境**: 你的AWS账户可能有其他同学的资源
2. **误删风险**: 错误的命令可能删除重要资源
3. **部署失败**: 需要能够快速回滚到之前的状态
4. **资源冲突**: 避免与其他项目的资源冲突

## 🔒 安全措施

### 1. 资源命名规范
```bash
# 所有资源都使用你的用户名前缀
USER_PREFIX="fuser23"  # 你的用户名
PROJECT_NAME="airline-order"

# 资源命名示例:
# EC2实例: fuser23-airline-order-instance
# 安全组: fuser23-airline-order-sg  
# 密钥对: fuser23-airline-order-keypair
# ECR仓库: fuser23-airline-order-app
```

### 2. 只创建必要资源
我们的脚本只会创建：
- ✅ 1个ECR仓库（存储Docker镜像）
- ✅ 1个EC2实例（t3.micro，免费套餐）
- ✅ 1个安全组（网络访问控制）
- ✅ 1个密钥对（SSH访问）

**不会创建**：
- ❌ VPC（使用默认VPC）
- ❌ RDS数据库（暂时使用容器内MySQL）
- ❌ 负载均衡器
- ❌ 其他复杂资源

### 3. 自动回滚机制

#### 部署前检查
```bash
# 检查是否有同名资源存在
check_existing_resources() {
    echo "🔍 检查现有资源..."
    
    # 检查EC2实例
    EXISTING_INSTANCES=$(aws ec2 describe-instances \
        --filters "Name=tag:Name,Values=$USER_PREFIX-$PROJECT_NAME-*" \
        --query 'Reservations[].Instances[?State.Name==`running`].InstanceId' \
        --output text)
    
    if [ ! -z "$EXISTING_INSTANCES" ]; then
        echo "⚠️ 发现现有实例: $EXISTING_INSTANCES"
        read -p "是否继续？这可能会影响现有部署 (y/n): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            echo "部署已取消"
            exit 0
        fi
    fi
}
```

#### 部署失败回滚
```bash
rollback_on_failure() {
    echo "🔄 检测到部署失败，开始回滚..."
    
    # 停止并删除失败的容器
    if [ ! -z "$INSTANCE_ID" ]; then
        echo "🛑 停止EC2实例..."
        aws ec2 stop-instances --instance-ids $INSTANCE_ID --region $AWS_REGION
        
        read -p "是否删除失败的EC2实例？(y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            aws ec2 terminate-instances --instance-ids $INSTANCE_ID --region $AWS_REGION
            echo "✅ EC2实例已删除"
        fi
    fi
    
    # 清理Docker镜像（可选）
    read -p "是否删除ECR中的镜像？(y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        aws ecr batch-delete-image \
            --repository-name $ECR_REPOSITORY \
            --image-ids imageTag=latest \
            --region $AWS_REGION || true
        echo "✅ ECR镜像已清理"
    fi
}
```

### 4. 资源清理脚本

#### 完全清理脚本
```bash
#!/bin/bash
# cleanup.sh - 清理所有创建的资源

USER_PREFIX="fuser23"
PROJECT_NAME="airline-order"
AWS_REGION="us-east-2"

echo "🧹 开始清理 $USER_PREFIX 的航空订单系统资源..."

# 1. 停止并删除EC2实例
echo "🛑 查找并停止EC2实例..."
INSTANCES=$(aws ec2 describe-instances \
    --filters "Name=tag:Name,Values=$USER_PREFIX-$PROJECT_NAME-*" \
    --query 'Reservations[].Instances[?State.Name!=`terminated`].InstanceId' \
    --output text \
    --region $AWS_REGION)

if [ ! -z "$INSTANCES" ]; then
    echo "找到实例: $INSTANCES"
    aws ec2 terminate-instances --instance-ids $INSTANCES --region $AWS_REGION
    echo "✅ EC2实例已终止"
fi

# 2. 删除安全组
echo "🔒 删除安全组..."
SG_ID=$(aws ec2 describe-security-groups \
    --filters "Name=group-name,Values=$USER_PREFIX-$PROJECT_NAME-sg" \
    --query 'SecurityGroups[0].GroupId' \
    --output text \
    --region $AWS_REGION)

if [ "$SG_ID" != "None" ] && [ ! -z "$SG_ID" ]; then
    aws ec2 delete-security-group --group-id $SG_ID --region $AWS_REGION
    echo "✅ 安全组已删除"
fi

# 3. 删除密钥对
echo "🔑 删除密钥对..."
aws ec2 delete-key-pair --key-name $USER_PREFIX-$PROJECT_NAME-keypair --region $AWS_REGION
rm -f $USER_PREFIX-$PROJECT_NAME-keypair.pem
echo "✅ 密钥对已删除"

# 4. 清理ECR仓库（可选）
read -p "是否删除ECR仓库？这将删除所有镜像 (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    aws ecr delete-repository \
        --repository-name $USER_PREFIX-$PROJECT_NAME-app \
        --force \
        --region $AWS_REGION
    echo "✅ ECR仓库已删除"
fi

echo "🎉 清理完成！"
```

## 🛠️ 安全部署步骤

### 第一步：预检查
```bash
# 1. 检查AWS凭证和权限
aws sts get-caller-identity

# 2. 检查当前区域的资源
aws ec2 describe-instances --region us-east-2
aws ecr describe-repositories --region us-east-2

# 3. 确认没有冲突的资源名称
```

### 第二步：最小化部署
```bash
# 只部署最基本的组件
./safe-deploy.sh --minimal
```

### 第三步：验证部署
```bash
# 检查服务是否正常运行
curl http://your-ec2-ip:8080/actuator/health

# 检查日志
ssh -i keypair.pem ec2-user@your-ec2-ip "docker logs airline-backend"
```

### 第四步：如果需要清理
```bash
# 运行清理脚本
./cleanup.sh
```

## 🔍 监控和日志

### 实时监控
```bash
# 监控EC2实例状态
watch -n 30 'aws ec2 describe-instances --instance-ids i-xxxxxxxxx --query "Reservations[0].Instances[0].State.Name"'

# 监控应用健康状态
watch -n 10 'curl -s http://your-ec2-ip:8080/actuator/health | jq .'
```

### 日志收集
```bash
# 收集应用日志
ssh -i keypair.pem ec2-user@your-ec2-ip "docker logs airline-backend > app.log 2>&1"
scp -i keypair.pem ec2-user@your-ec2-ip:app.log ./logs/

# 收集系统日志
ssh -i keypair.pem ec2-user@your-ec2-ip "sudo journalctl -u docker > docker.log"
```

## 💰 成本控制

### 免费套餐限制
- **EC2 t3.micro**: 750小时/月（约31天）
- **EBS存储**: 30GB/月
- **数据传输**: 15GB/月

### 自动停止脚本
```bash
#!/bin/bash
# auto-stop.sh - 每天晚上自动停止实例

# 添加到crontab: 0 22 * * * /path/to/auto-stop.sh
aws ec2 stop-instances --instance-ids $INSTANCE_ID --region us-east-2
echo "$(date): 实例已自动停止" >> /var/log/auto-stop.log
```

## 🚨 紧急情况处理

### 如果部署卡住
```bash
# 1. 检查实例状态
aws ec2 describe-instances --instance-ids i-xxxxxxxxx

# 2. 强制停止
aws ec2 stop-instances --instance-ids i-xxxxxxxxx --force

# 3. 如果无法停止，终止实例
aws ec2 terminate-instances --instance-ids i-xxxxxxxxx
```

### 如果误删了资源
```bash
# 1. 检查CloudTrail日志（如果启用）
aws logs describe-log-groups

# 2. 从备份恢复（如果有）
# 3. 重新部署（使用相同配置）
```

## ✅ 安全检查清单

部署前确认：
- [ ] 使用了用户名前缀避免冲突
- [ ] 检查了现有资源避免覆盖
- [ ] 设置了资源标签便于识别
- [ ] 准备了回滚和清理脚本
- [ ] 了解了成本和免费套餐限制
- [ ] 有紧急情况的联系方式

部署后确认：
- [ ] 应用正常运行
- [ ] 健康检查通过
- [ ] 日志正常输出
- [ ] 网络访问正常
- [ ] 资源使用在预期范围内

这样的安全部署方案可以最大程度地保护你和其他同学的资源！
