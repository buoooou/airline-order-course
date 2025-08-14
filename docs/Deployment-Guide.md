# 部署指南

## 环境要求

### 系统要求
- **操作系统**: Linux (推荐 Ubuntu 20.04+) / macOS / Windows
- **内存**: 最低 4GB，推荐 8GB+
- **存储**: 最低 10GB 可用空间
- **网络**: 稳定的互联网连接

### 软件要求
- **Java**: JDK 17 或更高版本
- **Node.js**: 18.0 或更高版本
- **npm**: 8.0 或更高版本
- **MySQL**: 8.0 或更高版本
- **Maven**: 3.6 或更高版本

## 开发环境部署

### 1. 环境准备

#### 1.1 安装 Java 17
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# macOS (使用 Homebrew)
brew install openjdk@17

# 验证安装
java -version
```

#### 1.2 安装 Node.js
```bash
# Ubuntu/Debian
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# macOS (使用 Homebrew)
brew install node@18

# 验证安装
node -v
npm -v
```

#### 1.3 安装 MySQL
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server

# macOS (使用 Homebrew)
brew install mysql

# 启动 MySQL 服务
sudo systemctl start mysql  # Linux
brew services start mysql   # macOS
```

#### 1.4 安装 Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS (使用 Homebrew)
brew install maven

# 验证安装
mvn -version
```

### 2. 数据库配置

#### 2.1 创建数据库
```sql
-- 登录 MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE airline_order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权
CREATE USER 'airline_user'@'localhost' IDENTIFIED BY 'airlineTest1234';
GRANT ALL PRIVILEGES ON airline_order_db.* TO 'airline_user'@'localhost';
FLUSH PRIVILEGES;

-- 使用数据库
USE airline_order_db;

-- 导入数据结构
SOURCE /path/to/flight-service/database-schema.sql;
```

#### 2.2 验证数据导入
```sql
-- 检查表是否创建成功
SHOW TABLES;

-- 检查管理员用户是否存在
SELECT * FROM app_users_yb WHERE role = 'ADMIN';
```

### 3. 后端部署

#### 3.1 配置应用参数
```bash
cd backend

# 编辑配置文件（可选）
vim src/main/resources/application.yml
```

#### 3.2 编译和运行
```bash
# 清理和编译
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/flight-service-1.0.0.jar
```

#### 3.3 验证后端部署
```bash
# 检查应用状态
curl http://localhost:8080/actuator/health

# 访问 API 文档
open http://localhost:8080/swagger-ui.html
```

### 4. 前端部署

#### 4.1 安装依赖
```bash
cd frontend
npm install
```

#### 4.2 启动开发服务器
```bash
# 开发模式启动
npm start

# 或者使用 Angular CLI
ng serve
```

#### 4.3 构建生产版本
```bash
# 构建生产版本
npm run build

# 构建文件将生成在 dist/ 目录
ls dist/frontend/
```

#### 4.4 验证前端部署
```bash
# 访问前端应用
open http://localhost:4200
```

## 生产环境部署

### 1. 使用 Docker 部署

#### 1.1 创建 Dockerfile (后端)
```dockerfile
# backend/Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/flight-service-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 1.2 创建 Dockerfile (前端)
```dockerfile
# frontend/Dockerfile
FROM node:18-alpine AS build

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
```

#### 1.3 创建 docker-compose.yml
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: airline_order_db
      MYSQL_USER: airline_user
      MYSQL_PASSWORD: airlineTest1234
      MYSQL_ROOT_PASSWORD: rootpass
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database-schema.sql:/docker-entrypoint-initdb.d/init.sql

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=mysql
      - DB_USER=airline_user
      - DB_PASS=airlineTest1234
    depends_on:
      - mysql

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

#### 1.4 启动 Docker 容器
```bash
# 构建并启动所有服务
docker-compose up -d

# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
```

### 2. 传统服务器部署

#### 2.1 后端部署到服务器

```bash
# 在服务器上创建应用目录
sudo mkdir -p /opt/airline-booking
sudo chown $USER:$USER /opt/airline-booking

# 上传 JAR 文件
scp target/flight-service-1.0.0.jar user@server:/opt/airline-booking/

# 创建系统服务
sudo tee /etc/systemd/system/airline-backend.service > /dev/null <<EOF
[Unit]
Description=Airline Booking Backend
After=network.target

[Service]
Type=simple
User=airline
WorkingDirectory=/opt/airline-booking
ExecStart=/usr/bin/java -jar flight-service-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 启动服务
sudo systemctl daemon-reload
sudo systemctl enable airline-backend
sudo systemctl start airline-backend
```

#### 2.2 前端部署到 Nginx

```bash
# 构建生产版本
npm run build

# 上传构建文件
scp -r dist/frontend/* user@server:/var/www/airline-booking/

# 配置 Nginx
sudo tee /etc/nginx/sites-available/airline-booking > /dev/null <<EOF
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/airline-booking;
    index index.html;

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# 启用站点
sudo ln -s /etc/nginx/sites-available/airline-booking /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 3. 云平台部署

#### 3.1 AWS 部署

```bash
# 使用 AWS CLI 部署到 Elastic Beanstalk
eb init airline-booking
eb create production
eb deploy
```

#### 3.2 阿里云部署

```bash
# 使用阿里云容器服务
aliyun ecs CreateInstance --ImageId ubuntu_20_04_x64 --InstanceType ecs.t5-small
```

## 监控和维护

### 1. 日志管理

#### 1.1 配置日志轮转
```bash
# 创建 logrotate 配置
sudo tee /etc/logrotate.d/airline-booking > /dev/null <<EOF
/opt/airline-booking/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 0644 airline airline
    postrotate
        systemctl reload airline-backend
    endscript
}
EOF
```

#### 1.2 设置日志监控
```bash
# 使用 journalctl 查看系统日志
journalctl -u airline-backend -f

# 查看应用日志
tail -f /opt/airline-booking/logs/flight-service.log
```

### 2. 性能监控

#### 2.1 使用 Spring Boot Actuator
```yaml
# 在 application.yml 中启用监控端点
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

#### 2.2 集成 Prometheus + Grafana
```yaml
# docker-compose-monitoring.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

### 3. 数据库维护

#### 3.1 定期备份
```bash
#!/bin/bash
# backup-db.sh
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u airline_user -p airline_order_db > backup_${DATE}.sql
```

#### 3.2 性能优化
```sql
-- 添加索引
CREATE INDEX idx_flights_departure_time ON flights_yb(departure_time);
CREATE INDEX idx_orders_booking_date ON orders_yb(booking_date);

-- 分析表
ANALYZE TABLE flights_yb, orders_yb, passengers_yb;
```

## 故障排除

### 1. 常见问题

#### 1.1 数据库连接失败
```bash
# 检查 MySQL 服务状态
sudo systemctl status mysql

# 检查端口是否开放
netstat -tlnp | grep 3306

# 检查防火墙设置
sudo ufw status
```

#### 1.2 内存不足
```bash
# 增加 JVM 堆内存
java -Xms1g -Xmx2g -jar flight-service-1.0.0.jar

# 监控内存使用
free -h
top -p $(pgrep java)
```

#### 1.3 端口冲突
```bash
# 查找占用端口的进程
sudo lsof -i :8080
sudo netstat -tlnp | grep 8080

# 修改应用端口
echo "server.port=8081" >> application.properties
```

### 2. 日志分析

#### 2.1 后端错误日志
```bash
# 查看最近的错误
grep -i error /opt/airline-booking/logs/flight-service.log | tail -20

# 查看特定时间段的日志
grep "2024-08-13 10:" /opt/airline-booking/logs/flight-service.log
```

#### 2.2 前端错误调试
```javascript
// 在浏览器控制台中查看错误
console.log('API 响应:', response);

// 检查网络请求
// 打开浏览器开发者工具 -> Network 标签
```

## 安全加固

### 1. 网络安全

```bash
# 配置防火墙
sudo ufw enable
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw deny 3306/tcp   # 禁止外部访问数据库
```

### 2. 应用安全

```yaml
# 生产环境配置
spring:
  profiles:
    active: production
  
security:
  require-ssl: true
  
app:
  jwt:
    secret: ${JWT_SECRET_PRODUCTION}
```

### 3. SSL 证书配置

```bash
# 使用 Let's Encrypt
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

---

如有部署问题，请参考故障排除章节或联系技术支持。