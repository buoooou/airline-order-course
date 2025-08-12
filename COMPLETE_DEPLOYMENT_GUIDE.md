# 🛫 航空订单系统 - 完整部署指南

## 📋 概览

这是一个完整的航空订单管理系统，包含Spring Boot后端、Angular前端，部署到AWS云平台。

### 🏗️ 系统架构
```
用户 → Nginx → Spring Boot应用 → AWS RDS MySQL
     ↓
   GitHub Actions CI/CD → AWS ECR → AWS EC2
```

### 🛠️ 技术栈
- **后端**: Spring Boot 2.7.18 + Java 11 + MySQL 8.0
- **前端**: Angular 20 + TypeScript
- **容器化**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **云平台**: AWS (EC2 + RDS + ECR)
- **反向代理**: Nginx

## 🚀 快速开始

### 第一步：准备AWS环境

#### 1.1 登录AWS控制台
```
URL: https://shida-awscloud3.signin.aws.amazon.com/console
用户名: FUser23
密码: p8Bd41^[
区域: us-east-2 (俄亥俄州)
账户ID: 381492153714
```

#### 1.2 配置AWS CLI
```bash
# 安装AWS CLI
brew install awscli  # macOS
# 或
sudo apt-get install awscli  # Linux

# 配置AWS凭证
aws configure
# 输入:
# AWS Access Key ID: [从IAM获取]
# AWS Secret Access Key: [从IAM获取]
# Default region name: us-east-2
# Default output format: json
```

#### 1.3 运行自动部署脚本
```bash
cd airline-order-course/aws-infrastructure
chmod +x deploy-infrastructure.sh
./deploy-infrastructure.sh
```

### 第二步：配置GitHub Secrets

在GitHub仓库的 Settings > Secrets and variables > Actions 中添加：

```
AWS_ACCESS_KEY_ID: [你的AWS访问密钥ID]
AWS_SECRET_ACCESS_KEY: [你的AWS秘密访问密钥]
EC2_HOST: [EC2实例的公网IP]
EC2_USERNAME: ec2-user
EC2_PRIVATE_KEY: [EC2密钥对的私钥内容]
DB_HOST: [RDS数据库端点]
DB_PASSWORD: [数据库密码]
JWT_SECRET: [JWT密钥]
```

### 第三步：推送代码触发部署

```bash
# 添加所有文件
git add .

# 提交代码
git commit -m "🚀 初始部署配置"

# 推送到main分支触发部署
git push origin main
```

## 📁 项目结构

```
airline-order-course/
├── 📁 backend/                    # Spring Boot后端
│   ├── 📁 src/main/java/         # Java源码
│   ├── 📁 src/main/resources/    # 配置文件
│   └── 📄 pom.xml                # Maven配置
├── 📁 frontend/                   # Angular前端
│   ├── 📁 src/                   # 前端源码
│   ├── 📄 package.json           # NPM配置
│   └── 📄 angular.json           # Angular配置
├── 📁 .github/workflows/         # CI/CD配置
│   ├── 📄 deploy.yml             # 主要部署流水线
│   └── 📄 aws-deploy.yml         # AWS专用部署
├── 📁 aws-infrastructure/        # AWS基础设施
│   ├── 📄 deploy-infrastructure.sh # 自动部署脚本
│   ├── 📄 cloudformation-template.yaml # CF模板
│   └── 📄 ec2-setup.sh           # EC2配置脚本
├── 📁 nginx/                     # Nginx配置
│   ├── 📄 nginx.conf             # 主配置文件
│   └── 📄 proxy_params           # 代理参数
├── 📄 Dockerfile                 # Docker镜像构建
├── 📄 docker-compose.yml         # 容器编排
├── 📄 proxy.conf.json            # 开发环境代理
└── 📄 README.md                  # 项目说明
```

## 🔧 详细配置说明

### 数据库配置

#### 生产环境 (application-prod.properties)
```properties
# AWS RDS配置
spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/airline_order_db?useSSL=true&serverTimezone=UTC
spring.datasource.username=airline_app
spring.datasource.password=${DB_PASSWORD}

# 连接池优化
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000
```

#### 开发环境 (application-local.properties)
```properties
# 本地MySQL配置
spring.datasource.url=jdbc:mysql://localhost:3306/airline_order_db?useSSL=false
spring.datasource.username=root
spring.datasource.password=rootroot
```

### 跨域配置

#### Nginx反向代理 (nginx.conf)
```nginx
server {
    listen 80;
    server_name _;
    
    # API代理
    location /api/ {
        proxy_pass http://backend:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
        # CORS配置
        add_header Access-Control-Allow-Origin * always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Authorization, Content-Type" always;
    }
}
```

#### Angular开发代理 (proxy.conf.json)
```json
{
  "/api/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "pathRewrite": {
      "^/api": ""
    }
  }
}
```

### Docker配置

#### 多阶段构建 (Dockerfile)
```dockerfile
# 前端构建阶段
FROM node:20-alpine AS frontend-builder
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install --legacy-peer-deps
COPY frontend/ ./
RUN npm run build -- --configuration=production --ssr=false

# 后端构建阶段
FROM maven:3.8.5-openjdk-11 AS backend-builder
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
COPY --from=frontend-builder /app/dist/frontend/browser/* ./src/main/resources/static/
RUN mvn clean package -DskipTests -B

# 运行时镜像
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=backend-builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 🔄 CI/CD流水线

### GitHub Actions工作流程

1. **代码检查和测试**
   - 前端代码检查和构建
   - 后端单元测试
   - 安全漏洞扫描

2. **Docker镜像构建**
   - 多阶段构建优化
   - 推送到AWS ECR
   - 镜像标签管理

3. **自动部署**
   - SSH连接到EC2实例
   - 拉取最新镜像
   - 更新Docker Compose配置
   - 滚动更新服务

4. **部署后测试**
   - 健康检查验证
   - API接口测试
   - 服务可用性确认

### 部署触发条件
- **main分支**: 自动部署到生产环境
- **develop分支**: 自动部署到开发环境
- **Pull Request**: 仅运行测试，不部署

## 🌐 访问地址

部署完成后，你可以通过以下地址访问系统：

```
🏠 主页: http://your-ec2-ip/
📚 API文档: http://your-ec2-ip/swagger-ui/index.html
💚 健康检查: http://your-ec2-ip/api/actuator/health
🔧 API接口: http://your-ec2-ip/api/
```

## 📊 监控和维护

### 日志查看
```bash
# SSH连接到EC2
ssh -i your-key.pem ec2-user@your-ec2-ip

# 查看应用日志
cd /opt/airline-order
docker-compose logs -f backend

# 查看Nginx日志
docker-compose logs -f nginx
```

### 服务管理
```bash
# 重启服务
docker-compose restart

# 更新服务
docker-compose pull
docker-compose up -d

# 查看服务状态
docker-compose ps
```

### 数据库管理
```bash
# 连接到RDS数据库
mysql -h your-rds-endpoint -u airline_app -p airline_order_db

# 备份数据库
mysqldump -h your-rds-endpoint -u airline_app -p airline_order_db > backup.sql

# 恢复数据库
mysql -h your-rds-endpoint -u airline_app -p airline_order_db < backup.sql
```

## 💰 成本优化

### 免费套餐资源
- **EC2 t3.micro**: 750小时/月（免费12个月）
- **RDS db.t3.micro**: 750小时/月（免费12个月）
- **EBS存储**: 30GB/月（免费12个月）
- **数据传输**: 15GB/月（永久免费）

### 预估月费用
```
开发环境:
- EC2 t3.micro: $0 (免费套餐)
- RDS db.t3.micro: $0 (免费套餐)
- EBS 20GB: $2
- 数据传输: $1
总计: ~$3/月

生产环境:
- EC2 t3.small: $17
- RDS db.t3.small: $25
- EBS 50GB: $5
- 数据传输: $5
总计: ~$52/月
```

## 🚨 故障排除

### 常见问题

#### 1. 部署失败
```bash
# 检查GitHub Actions日志
# 查看EC2实例状态
aws ec2 describe-instances --instance-ids i-xxxxxxxxx

# 检查安全组配置
aws ec2 describe-security-groups --group-ids sg-xxxxxxxxx
```

#### 2. 数据库连接失败
```bash
# 测试数据库连接
mysql -h your-rds-endpoint -u airline_app -p

# 检查安全组规则
aws rds describe-db-instances --db-instance-identifier your-db-name
```

#### 3. 应用无法访问
```bash
# 检查容器状态
docker-compose ps

# 查看应用日志
docker-compose logs backend

# 测试端口连通性
telnet your-ec2-ip 8080
```

### 性能优化

#### 1. 数据库优化
```sql
-- 创建索引
CREATE INDEX idx_flights_date ON flights(departure_date);
CREATE INDEX idx_orders_user ON orders(user_id);

-- 分析表
ANALYZE TABLE flights, orders, users;
```

#### 2. 应用优化
```properties
# JVM参数优化
-Xmx512m -Xms256m
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
```

#### 3. Nginx优化
```nginx
# 启用gzip压缩
gzip on;
gzip_types text/plain application/json application/javascript text/css;

# 启用缓存
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

## 📚 相关文档

- [部署计划详解](DEPLOYMENT_PLAN.md)
- [跨域配置指南](CORS_AND_PROXY_GUIDE.md)
- [数据库迁移指南](DATABASE_MIGRATION_GUIDE.md)
- [AWS基础设施脚本](aws-infrastructure/deploy-infrastructure.sh)

## 🎯 下一步计划

1. **SSL证书配置**: 使用AWS Certificate Manager
2. **域名配置**: 配置Route53域名解析
3. **CDN加速**: 配置CloudFront分发
4. **监控告警**: 配置CloudWatch告警
5. **自动扩展**: 配置Auto Scaling Group
6. **负载均衡**: 配置Application Load Balancer

## 📞 技术支持

如果在部署过程中遇到问题，请检查：

1. **GitHub Actions日志**: 查看构建和部署日志
2. **AWS CloudWatch**: 查看EC2和RDS监控指标
3. **应用日志**: SSH到EC2查看容器日志
4. **网络配置**: 检查安全组和VPC配置

---

🎉 **恭喜！你已经成功部署了航空订单系统到AWS云平台！**

现在你可以：
- ✅ 通过CI/CD自动部署代码更新
- ✅ 使用AWS RDS管理数据库
- ✅ 通过Nginx处理跨域和负载均衡
- ✅ 监控应用性能和健康状态
- ✅ 扩展系统以支持更多用户

继续优化和完善你的系统吧！🚀
