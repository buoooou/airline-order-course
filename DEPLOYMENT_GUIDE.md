# 航空订单管理系统 - 完整部署指南

## 🏗️ 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端 Angular  │    │  后端 Spring    │    │   MySQL 数据库  │
│   (Port 80)     │────│   (Port 8080)   │────│   (Port 3306)   │
│   Nginx 反向代理│    │   JWT 认证      │    │   持久化存储    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 AWS EC2 环境准备

### 1. EC2 实例配置
```bash
# 实例类型: t3.medium (2 vCPU, 4GB RAM)
# 操作系统: Amazon Linux 2023
# 存储: 30GB gp3 (可扩展到 50GB)
# 安全组端口: 22, 80, 8080, 3306
```

### 2. 磁盘扩展 (如需要)
```bash
# 查看当前磁盘使用情况
df -h

# 扩展分区 (假设从30GB扩展到50GB)
sudo growpart /dev/xvda1 1
sudo xfs_growfs -d /

# 验证扩展结果
df -h
```

### 3. Docker 环境安装
```bash
# 更新系统
sudo yum update -y

# 安装 Docker
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -a -G docker ec2-user

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 重新登录以应用组权限
exit
```

### 4. AWS CLI 和 ECR 配置
```bash
# 安装 AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# 配置 AWS 凭证
aws configure
# AWS Access Key ID: [你的访问密钥]
# AWS Secret Access Key: [你的秘密密钥]
# Default region name: us-east-2
# Default output format: json

# 登录到 ECR
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 381492153714.dkr.ecr.us-east-2.amazonaws.com
```

## 📦 容器镜像构建

### 1. 后端镜像构建 (AMD64架构)
```bash
# 构建后端镜像
podman build --platform=linux/amd64 -f Dockerfile.amd64 -t 381492153714.dkr.ecr.us-east-2.amazonaws.com/fuser23-airline-order-backend:latest .

# 推送到 ECR
podman push 381492153714.dkr.ecr.us-east-2.amazonaws.com/fuser23-airline-order-backend:latest
```

### 2. 前端镜像构建 (AMD64架构)
```bash
# 构建前端镜像
podman build --platform=linux/amd64 -f frontend.Dockerfile -t 381492153714.dkr.ecr.us-east-2.amazonaws.com/fuser23-airline-order-frontend:latest .

# 推送到 ECR
podman push 381492153714.dkr.ecr.us-east-2.amazonaws.com/fuser23-airline-order-frontend:latest
```

## 🔧 部署配置文件

### 1. 生产环境 Docker Compose
参考: `docker-compose.aws.yml`

### 2. Nginx 配置
参考: `nginx/nginx.docker.conf`

### 3. 环境变量配置
```bash
# 后端环境变量
SPRING_PROFILES_ACTIVE=production
MYSQL_HOST=airline-mysql
MYSQL_DATABASE=airline_order_db
MYSQL_USER=airline
MYSQL_PASSWORD=airlineTest1234

# 前端环境变量 (构建时)
API_URL=http://18.116.240.81:8080
```

## 🚀 自动化部署脚本

### 1. 安全部署脚本
参考: `safe-deploy.sh`
- 自动备份当前版本
- 健康检查
- 回滚机制

### 2. 前端专用部署脚本
参考: `safe-frontend-deploy.sh`
- 前端独立部署
- API 配置验证

## 🔍 系统监控和维护

### 1. 容器状态检查
```bash
# 查看所有容器状态
docker ps -a

# 查看容器日志
docker logs airline-backend
docker logs airline-frontend
docker logs airline-mysql

# 查看资源使用情况
docker stats
```

### 2. 数据库维护
```bash
# 连接数据库
docker exec -it airline-mysql mysql -u airline -p airline_order_db

# 备份数据库
docker exec airline-mysql mysqldump -u airline -p airline_order_db > backup_$(date +%Y%m%d_%H%M%S).sql

# 恢复数据库
docker exec -i airline-mysql mysql -u airline -p airline_order_db < backup_file.sql
```

### 3. 日志管理
```bash
# 清理 Docker 日志
sudo truncate -s 0 /var/lib/docker/containers/*/*-json.log

# 设置日志轮转
sudo tee /etc/docker/daemon.json > /dev/null <<EOF
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
EOF

sudo systemctl restart docker
```

## 🛡️ 安全配置

### 1. 防火墙配置
```bash
# 配置 iptables (如需要)
sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 22 -j ACCEPT
```

### 2. SSL/HTTPS 配置 (可选)
```bash
# 安装 Certbot
sudo yum install -y certbot python3-certbot-nginx

# 获取 SSL 证书
sudo certbot --nginx -d yourdomain.com

# 自动续期
sudo crontab -e
# 添加: 0 12 * * * /usr/bin/certbot renew --quiet
```

## 📊 性能优化

### 1. 系统优化
```bash
# 增加文件描述符限制
echo "* soft nofile 65536" | sudo tee -a /etc/security/limits.conf
echo "* hard nofile 65536" | sudo tee -a /etc/security/limits.conf

# 优化内核参数
echo "net.core.somaxconn = 65535" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

### 2. 数据库优化
```sql
-- MySQL 配置优化
SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB
SET GLOBAL max_connections = 200;
SET GLOBAL query_cache_size = 67108864; -- 64MB
```

## 🔄 故障排除

### 1. 常见问题
```bash
# 容器无法启动
docker logs [container_name]

# 端口被占用
sudo netstat -tulpn | grep :8080

# 磁盘空间不足
df -h
docker system prune -f

# 内存不足
free -h
docker stats
```

### 2. 应急处理
```bash
# 快速重启所有服务
docker-compose -f docker-compose.aws.yml restart

# 强制重新部署
docker-compose -f docker-compose.aws.yml down
docker-compose -f docker-compose.aws.yml up -d

# 数据库紧急恢复
docker exec -i airline-mysql mysql -u root -p < emergency_backup.sql
```

## 📈 扩展性考虑

### 1. 负载均衡
- 使用 AWS Application Load Balancer
- 配置多个 EC2 实例
- 实现会话粘性

### 2. 数据库扩展
- 读写分离
- 数据库集群
- 缓存层 (Redis)

### 3. 容器编排
- 迁移到 AWS ECS 或 EKS
- 实现自动扩缩容
- 服务网格

## 🎯 最佳实践

1. **定期备份**: 每日自动备份数据库和配置文件
2. **监控告警**: 设置 CloudWatch 监控和告警
3. **版本管理**: 使用语义化版本号标记镜像
4. **安全更新**: 定期更新系统和依赖包
5. **文档维护**: 保持部署文档的及时更新

## 📞 支持联系

如遇到部署问题，请检查：
1. 容器日志
2. 网络连接
3. 端口配置
4. 环境变量
5. 磁盘空间

---

**部署完成后访问地址:**
- 前端应用: http://18.116.240.81
- 后端API: http://18.116.240.81:8080
- API文档: http://18.116.240.81:8080/swagger-ui/index.html
