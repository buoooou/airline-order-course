# 🚀 航空订单系统 - 完整部署指南

## 📋 目录
1. [项目概述](#项目概述)
2. [AWS基础设施准备](#aws基础设施准备)
3. [CI/CD配置](#cicd配置)
4. [跨域和代理配置](#跨域和代理配置)
5. [数据库配置](#数据库配置)
6. [部署步骤](#部署步骤)
7. [监控和维护](#监控和维护)
8. [故障排除](#故障排除)

## 🎯 项目概述

### 系统架构
```
┌─────────────────────────────────────────────────────────────┐
│                    用户访问层                                │
├─────────────────────────────────────────────────────────────┤
│  CloudFront CDN  │  Route 53 DNS  │  Certificate Manager   │
├─────────────────────────────────────────────────────────────┤
│                    负载均衡层                                │
├─────────────────────────────────────────────────────────────┤
│              Application Load Balancer                     │
├─────────────────────────────────────────────────────────────┤
│                    应用服务层                                │
├─────────────────────────────────────────────────────────────┤
│  EC2 Instance 1  │  EC2 Instance 2  │  Auto Scaling Group  │
│  (Docker容器)    │  (Docker容器)    │                      │
├─────────────────────────────────────────────────────────────┤
│                    数据存储层                                │
├─────────────────────────────────────────────────────────────┤
│     RDS MySQL    │     Redis        │      S3 Storage      │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈
- **前端**: Angular 20 + Angular Material
- **后端**: Spring Boot 2.7 + MySQL 8.0
- **容器化**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **云平台**: AWS (EC2, RDS, S3, ALB)
- **反向代理**: Nginx
- **监控**: Prometheus + Grafana

## 🏗️ AWS基础设施准备

### 方案一：使用CloudFormation自动化部署（推荐）

1. **登录AWS控制台**
   ```bash
   # 使用你的AWS账号信息
   用户名: FUser23
   密码: p8Bd41^[
   登录页面: https://shida-awscloud3.signin.aws.amazon.com/console
   ```

2. **部署CloudFormation模板**
   ```bash
   # 1. 进入CloudFormation服务
   # 2. 点击"创建堆栈"
   # 3. 上传 aws-infrastructure/cloudformation-template.yaml
   # 4. 填写参数：
   #    - Environment: prod
   #    - InstanceType: t3.medium
   #    - KeyPairName: 你的密钥对名称
   #    - DBUsername: airline_admin
   #    - DBPassword: 设置强密码
   ```

### 方案二：手动创建AWS资源

#### 2.1 创建VPC和网络
```bash
# 1. 创建VPC (10.0.0.0/16)
# 2. 创建公有子网 (10.0.1.0/24, 10.0.2.0/24)
# 3. 创建私有子网 (10.0.3.0/24, 10.0.4.0/24)
# 4. 创建Internet Gateway
# 5. 创建NAT Gateway
# 6. 配置路由表
```

#### 2.2 创建安全组
```bash
# 应用服务器安全组
# - 入站: SSH(22), HTTP(80), HTTPS(443), App(8080)
# - 出站: 全部允许

# 数据库安全组
# - 入站: MySQL(3306) 仅来自应用服务器安全组
# - 出站: 无
```

#### 2.3 创建EC2实例
```bash
# 1. 选择Ubuntu 22.04 LTS AMI
# 2. 实例类型: t3.medium (2核4GB)
# 3. 配置网络: 公有子网
# 4. 存储: 20GB GP3
# 5. 安全组: 应用服务器安全组
```

#### 2.4 创建RDS数据库
```bash
# 1. 引擎: MySQL 8.0
# 2. 实例类型: db.t3.micro
# 3. 存储: 20GB GP2
# 4. 网络: 私有子网
# 5. 安全组: 数据库安全组
```

## 🔧 CI/CD配置

### 3.1 准备部署密钥

1. **生成SSH密钥对**
   ```bash
   # 在本地执行
   ssh-keygen -t rsa -b 4096 -f github_actions_deploy_key -N ""
   
   # 这会生成两个文件：
   # - github_actions_deploy_key (私钥)
   # - github_actions_deploy_key.pub (公钥)
   ```

2. **配置EC2服务器**
   ```bash
   # 登录EC2服务器
   ssh -i your-key.pem ubuntu@your-ec2-ip
   
   # 将公钥添加到authorized_keys
   echo "公钥内容" >> ~/.ssh/authorized_keys
   chmod 600 ~/.ssh/authorized_keys
   chmod 700 ~/.ssh
   ```

3. **运行服务器初始化脚本**
   ```bash
   # 在EC2服务器上执行
   wget https://raw.githubusercontent.com/your-repo/airline-order-course/main/aws-infrastructure/ec2-setup.sh
   chmod +x ec2-setup.sh
   ./ec2-setup.sh
   ```

### 3.2 配置GitHub Secrets

在GitHub仓库的 Settings > Secrets and variables > Actions 中添加：

| Secret名称 | 值 | 说明 |
|-----------|----|----|
| `DOCKERHUB_USERNAME` | 你的Docker Hub用户名 | 用于推送镜像 |
| `DOCKERHUB_TOKEN` | Docker Hub访问令牌 | 用于认证 |
| `EC2_HOST` | EC2实例公网IP | 部署目标服务器 |
| `EC2_USERNAME` | ubuntu | EC2登录用户名 |
| `SSH_PRIVATE_KEY` | 私钥完整内容 | SSH认证 |

### 3.3 配置Docker Hub

1. **创建Docker Hub仓库**
   ```bash
   # 登录 https://hub.docker.com
   # 创建仓库: airline-order-system
   ```

2. **生成访问令牌**
   ```bash
   # Account Settings > Security > New Access Token
   # 权限: Read, Write, Delete
   ```

## 🌐 跨域和代理配置

### 4.1 前端跨域配置

更新 `frontend/src/environments/environment.prod.ts`:
```typescript
export const environment = {
  production: true,
  // 使用你的实际域名或EC2 IP
  apiUrl: 'https://your-domain.com/api',
  // 或者: apiUrl: 'http://your-ec2-ip:8080/api',
};
```

### 4.2 后端CORS配置

在Spring Boot中添加CORS配置：
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:4200",
                    "https://your-domain.com",
                    "http://your-ec2-ip"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### 4.3 Nginx代理配置

已包含在 `nginx/nginx.conf` 中，主要配置：
```nginx
# API代理
location /api/ {
    proxy_pass http://airline_backend;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}

# 静态资源
location / {
    try_files $uri $uri/ @backend;
}
```

## 🗄️ 数据库配置

### 5.1 RDS MySQL配置

1. **连接信息**
   ```properties
   # 在 application-prod.properties 中
   spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/airline_order_db
   spring.datasource.username=airline_admin
   spring.datasource.password=your-strong-password
   ```

2. **初始化数据库**
   ```sql
   -- 连接到RDS实例
   mysql -h your-rds-endpoint -u airline_admin -p
   
   -- 创建数据库
   CREATE DATABASE airline_order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   -- 创建应用用户
   CREATE USER 'airline_user'@'%' IDENTIFIED BY 'AirlinePass2024!';
   GRANT ALL PRIVILEGES ON airline_order_db.* TO 'airline_user'@'%';
   FLUSH PRIVILEGES;
   ```

### 5.2 本地数据库配置（开发环境）

```bash
# 使用Docker运行本地MySQL
docker run -d \
  --name airline-mysql \
  -e MYSQL_ROOT_PASSWORD=rootroot \
  -e MYSQL_DATABASE=airline_order_db \
  -e MYSQL_USER=airline_user \
  -e MYSQL_PASSWORD=AirlinePass2024! \
  -p 3306:3306 \
  mysql:8.0
```

## 🚀 部署步骤

### 6.1 准备工作检查清单

- [ ] AWS账号已准备
- [ ] EC2实例已创建并配置
- [ ] RDS数据库已创建
- [ ] GitHub Secrets已配置
- [ ] Docker Hub仓库已创建
- [ ] 域名已配置（可选）

### 6.2 执行部署

1. **推送代码触发CI/CD**
   ```bash
   git add .
   git commit -m "feat: 添加CI/CD配置和AWS部署文件"
   git push origin main
   ```

2. **监控部署过程**
   ```bash
   # 在GitHub Actions页面监控部署进度
   # 检查各个阶段的日志输出
   ```

3. **验证部署结果**
   ```bash
   # 检查应用健康状态
   curl http://your-ec2-ip:8080/actuator/health
   
   # 检查前端页面
   curl http://your-ec2-ip:8080/
   
   # 检查API接口
   curl http://your-ec2-ip:8080/api/flights
   ```

### 6.3 配置域名（可选）

1. **Route 53配置**
   ```bash
   # 1. 创建托管区域
   # 2. 添加A记录指向ALB
   # 3. 配置SSL证书
   ```

2. **SSL证书配置**
   ```bash
   # 使用AWS Certificate Manager
   # 1. 申请SSL证书
   # 2. 验证域名所有权
   # 3. 配置ALB使用证书
   ```

## 📊 监控和维护

### 7.1 应用监控

1. **健康检查**
   ```bash
   # 应用健康检查
   curl http://your-ec2-ip:8080/actuator/health
   
   # 详细信息
   curl http://your-ec2-ip:8080/actuator/info
   
   # 指标信息
   curl http://your-ec2-ip:8080/actuator/metrics
   ```

2. **日志监控**
   ```bash
   # 查看应用日志
   docker logs airline-order-app
   
   # 查看数据库日志
   docker logs airline-mysql-db
   
   # 查看Nginx日志
   docker logs airline-nginx
   ```

### 7.2 性能监控

1. **Prometheus + Grafana**
   ```bash
   # 访问Prometheus
   http://your-ec2-ip:9090
   
   # 访问Grafana
   http://your-ec2-ip:3000
   # 默认用户名/密码: admin/admin123
   ```

2. **系统监控**
   ```bash
   # CPU和内存使用情况
   htop
   
   # 磁盘使用情况
   df -h
   
   # 网络连接
   netstat -tulpn
   ```

### 7.3 备份策略

1. **数据库备份**
   ```bash
   # 自动备份脚本已配置在 ~/airline-deployment/backup.sh
   # 每天凌晨2点自动执行
   
   # 手动备份
   ./backup.sh
   ```

2. **应用配置备份**
   ```bash
   # 备份配置文件
   tar -czf config-backup-$(date +%Y%m%d).tar.gz ~/airline-deployment/
   ```

## 🔧 故障排除

### 8.1 常见问题

#### 问题1：CI/CD部署失败
```bash
# 检查GitHub Actions日志
# 常见原因：
# - SSH连接失败：检查密钥配置
# - Docker镜像构建失败：检查Dockerfile
# - 服务启动失败：检查配置文件
```

#### 问题2：应用无法访问
```bash
# 检查服务状态
docker-compose ps

# 检查端口监听
netstat -tulpn | grep 8080

# 检查防火墙
sudo ufw status

# 检查安全组配置
```

#### 问题3：数据库连接失败
```bash
# 检查数据库服务
docker logs airline-mysql-db

# 测试数据库连接
mysql -h database -u airline_user -p

# 检查网络连接
docker network ls
docker network inspect airline-deployment_airline-network
```

#### 问题4：前端页面404
```bash
# 检查静态文件
docker exec airline-order-app ls -la /app/static/

# 检查Nginx配置
docker exec airline-nginx nginx -t

# 重启服务
docker-compose restart app nginx
```

### 8.2 性能优化

1. **数据库优化**
   ```sql
   -- 查看慢查询
   SHOW VARIABLES LIKE 'slow_query_log';
   
   -- 优化索引
   SHOW INDEX FROM your_table;
   
   -- 分析查询
   EXPLAIN SELECT * FROM your_table WHERE condition;
   ```

2. **应用优化**
   ```bash
   # JVM参数调优
   JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"
   
   # 连接池优化
   spring.datasource.hikari.maximum-pool-size=20
   spring.datasource.hikari.minimum-idle=5
   ```

3. **缓存优化**
   ```bash
   # Redis缓存配置
   spring.cache.type=redis
   spring.cache.redis.time-to-live=3600000
   ```

## 📝 维护清单

### 日常维护
- [ ] 检查应用健康状态
- [ ] 查看错误日志
- [ ] 监控系统资源使用情况
- [ ] 检查备份是否正常

### 周期维护
- [ ] 更新系统补丁
- [ ] 清理旧的Docker镜像
- [ ] 检查SSL证书有效期
- [ ] 性能测试和优化

### 安全维护
- [ ] 更新密码和密钥
- [ ] 检查安全组配置
- [ ] 审查访问日志
- [ ] 更新依赖包版本

## 🎉 部署完成

恭喜！你的航空订单系统已经成功部署到AWS云平台。

**访问地址：**
- 应用首页: `http://your-ec2-ip:8080`
- API文档: `http://your-ec2-ip:8080/swagger-ui.html`
- 监控面板: `http://your-ec2-ip:3000`

**下一步：**
1. 配置自定义域名
2. 设置SSL证书
3. 配置CDN加速
4. 添加更多监控指标
5. 实施自动化测试

如有问题，请参考故障排除章节或联系技术支持。
