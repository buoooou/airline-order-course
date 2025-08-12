# 航空订单系统 - 完整CI/CD和AWS部署方案

## 📋 项目概览

### 技术栈
- **后端**: Spring Boot 2.7.18 + Java 11 + MySQL
- **前端**: Angular 20 + TypeScript
- **容器化**: Docker/Podman
- **数据库**: MySQL 8.0
- **部署**: AWS EC2 + RDS + CloudFront + Route53

### 架构图
```
用户 → CloudFront → ALB → EC2实例 → RDS MySQL
     ↓
   Route53 (域名解析)
```

## 🚀 第一阶段：CI/CD流水线设置

### 1.1 GitHub Actions配置

我们已经创建了完整的CI/CD流水线，包括：
- 自动构建和测试
- Docker镜像构建和推送到ECR
- 自动部署到EC2
- 健康检查和回滚机制

### 1.2 AWS ECR设置
```bash
# 创建ECR仓库
aws ecr create-repository --repository-name airline-order-app --region us-east-1
```

## 🏗️ 第二阶段：AWS基础设施

### 2.1 网络架构
- **VPC**: 10.0.0.0/16
- **公有子网**: 10.0.1.0/24, 10.0.2.0/24 (多AZ)
- **私有子网**: 10.0.3.0/24, 10.0.4.0/24 (数据库)
- **Internet Gateway**: 公网访问
- **NAT Gateway**: 私有子网出网

### 2.2 安全组配置
- **ALB安全组**: 80, 443端口开放
- **EC2安全组**: 8080端口仅允许ALB访问
- **RDS安全组**: 3306端口仅允许EC2访问

### 2.3 EC2实例配置
- **实例类型**: t3.medium (2vCPU, 4GB RAM)
- **操作系统**: Amazon Linux 2023
- **存储**: 20GB gp3 SSD
- **自动扩展**: 最小1台，最大3台

### 2.4 RDS数据库配置
- **引擎**: MySQL 8.0
- **实例类型**: db.t3.micro (开发) / db.t3.small (生产)
- **存储**: 20GB gp2，自动扩展至100GB
- **多AZ部署**: 生产环境启用
- **备份**: 7天保留期，自动备份

## 🔧 第三阶段：应用配置

### 3.1 环境变量配置
```bash
# 生产环境变量
SPRING_PROFILES_ACTIVE=prod
DB_HOST=your-rds-endpoint.amazonaws.com
DB_NAME=airline_order_db
DB_USERNAME=admin
DB_PASSWORD=your-secure-password
JWT_SECRET=your-jwt-secret-key
```

### 3.2 跨域配置
我们已经配置了proxy.conf.json和nginx反向代理来处理跨域问题。

### 3.3 SSL证书
- 使用AWS Certificate Manager申请免费SSL证书
- 配置CloudFront和ALB使用HTTPS

## 📊 第四阶段：监控和日志

### 4.1 CloudWatch监控
- CPU使用率
- 内存使用率
- 数据库连接数
- 应用响应时间

### 4.2 日志管理
- 应用日志发送到CloudWatch Logs
- 访问日志分析
- 错误告警设置

## 💰 成本估算

### 开发环境 (月费用)
- EC2 t3.micro: $8.5
- RDS db.t3.micro: $15
- ALB: $16
- 数据传输: $5
- **总计**: ~$45/月

### 生产环境 (月费用)
- EC2 t3.medium x2: $60
- RDS db.t3.small: $25
- ALB: $16
- CloudFront: $10
- Route53: $0.5
- **总计**: ~$110/月

## 🔐 安全最佳实践

1. **IAM角色**: 最小权限原则
2. **VPC**: 网络隔离
3. **加密**: 数据传输和存储加密
4. **备份**: 自动备份和快照
5. **更新**: 定期安全更新

## 📈 扩展策略

### 水平扩展
- Auto Scaling Group
- Application Load Balancer
- RDS读副本

### 垂直扩展
- 升级EC2实例类型
- 增加RDS实例规格

## 🚀 部署步骤

### 步骤1: 准备AWS环境
1. 创建AWS账户
2. 配置IAM用户和权限
3. 设置AWS CLI

### 步骤2: 基础设施部署
1. 运行CloudFormation模板
2. 配置安全组和网络
3. 创建RDS实例

### 步骤3: 应用部署
1. 推送代码到GitHub
2. 触发CI/CD流水线
3. 验证部署结果

### 步骤4: 域名和SSL
1. 配置Route53域名
2. 申请SSL证书
3. 配置CloudFront

## 🔍 故障排除

### 常见问题
1. **数据库连接失败**: 检查安全组和网络配置
2. **应用启动失败**: 查看CloudWatch日志
3. **跨域问题**: 检查nginx配置和CORS设置

### 监控指标
- 应用健康检查
- 数据库性能指标
- 网络延迟监控

## 📞 支持和维护

### 日常维护
- 定期备份验证
- 安全补丁更新
- 性能优化调整

### 紧急响应
- 24/7监控告警
- 自动故障转移
- 快速回滚机制
