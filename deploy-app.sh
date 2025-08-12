#!/bin/bash
set -e

echo "🔄 开始部署航空订单系统..."

cd /opt/airline-order

# 设置环境变量
export AWS_DEFAULT_REGION=us-east-2
export ECR_REGISTRY=381492153714.dkr.ecr.us-east-2.amazonaws.com
export ECR_REPOSITORY=fuser23-airline-order-app
export DB_HOST=localhost  # 暂时使用本地数据库
export DB_USERNAME=airline_app
export DB_PASSWORD=AirlineApp2024!
export JWT_SECRET=63ffbc2b8d13ad5180ed7ae7c67f18c85d86046732fc9ced6a02a9d50abb1a03

# 登录ECR
echo "🔑 登录到ECR..."
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin $ECR_REGISTRY

# 拉取最新镜像
echo "📥 拉取最新镜像..."
docker pull $ECR_REGISTRY/$ECR_REPOSITORY:latest

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
        echo "⏳ 等待应用启动... ($i/15)"
        sleep 20
    fi
done

# 显示访问信息
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
echo "🎉 部署完成！"
echo "🌐 访问地址: http://$PUBLIC_IP"
echo "📚 API文档: http://$PUBLIC_IP/swagger-ui/index.html"
echo "💚 健康检查: http://$PUBLIC_IP/api/actuator/health"
