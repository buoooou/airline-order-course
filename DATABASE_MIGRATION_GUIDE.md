# 航空订单系统 - 数据库迁移到AWS RDS指南

## 📊 数据库迁移概览

### 当前状态
- **本地数据库**: MySQL 8.0 (localhost:3306)
- **远程数据库**: MySQL 8.0 (18.207.201.92:3306) 
- **目标**: AWS RDS MySQL 8.0

### 迁移策略
1. **数据备份**: 导出现有数据
2. **RDS创建**: 配置AWS RDS实例
3. **数据导入**: 迁移数据到RDS
4. **应用配置**: 更新连接配置
5. **测试验证**: 确保功能正常

## 🗄️ 第一步：数据备份

### 1.1 备份当前数据库
```bash
# 备份远程数据库
mysqldump -h 18.207.201.92 -P 3306 -u airline -p airline_order_db > airline_order_backup.sql

# 备份本地数据库（如果有数据）
mysqldump -h localhost -P 3306 -u root -p airline_order_db > airline_order_local_backup.sql

# 只备份结构（不包含数据）
mysqldump -h 18.207.201.92 -P 3306 -u airline -p --no-data airline_order_db > airline_order_schema.sql

# 只备份数据（不包含结构）
mysqldump -h 18.207.201.92 -P 3306 -u airline -p --no-create-info airline_order_db > airline_order_data.sql
```

### 1.2 验证备份文件
```bash
# 检查备份文件大小
ls -lh airline_order_*.sql

# 查看备份文件内容
head -20 airline_order_backup.sql
tail -20 airline_order_backup.sql

# 统计表数量
grep -c "CREATE TABLE" airline_order_backup.sql
```

## 🏗️ 第二步：AWS RDS配置

### 2.1 RDS实例规格选择

#### 开发环境（推荐）
```yaml
实例类型: db.t3.micro
存储类型: gp2
存储大小: 20 GB
多AZ部署: 否
备份保留: 7天
成本: ~$15/月（免费套餐内）
```

#### 生产环境
```yaml
实例类型: db.t3.small
存储类型: gp3
存储大小: 100 GB
多AZ部署: 是
备份保留: 30天
成本: ~$50/月
```

### 2.2 RDS安全组配置
```bash
# 创建RDS安全组
aws ec2 create-security-group \
    --group-name airline-rds-sg \
    --description "航空订单系统RDS安全组" \
    --vpc-id vpc-xxxxxxxxx \
    --region us-east-2

# 允许EC2访问MySQL端口
aws ec2 authorize-security-group-ingress \
    --group-id sg-xxxxxxxxx \
    --protocol tcp \
    --port 3306 \
    --source-group sg-yyyyyyyyy \
    --region us-east-2

# 允许本地开发访问（临时，生产环境应删除）
aws ec2 authorize-security-group-ingress \
    --group-id sg-xxxxxxxxx \
    --protocol tcp \
    --port 3306 \
    --cidr 0.0.0.0/0 \
    --region us-east-2
```

### 2.3 创建RDS实例
```bash
# 使用AWS CLI创建RDS实例
aws rds create-db-instance \
    --db-instance-identifier airline-order-db \
    --db-instance-class db.t3.micro \
    --engine mysql \
    --engine-version 8.0.35 \
    --master-username admin \
    --master-user-password 'AirlineRDS2024!' \
    --allocated-storage 20 \
    --storage-type gp2 \
    --vpc-security-group-ids sg-xxxxxxxxx \
    --db-subnet-group-name default-vpc-xxxxxxxxx \
    --backup-retention-period 7 \
    --storage-encrypted \
    --region us-east-2

# 等待RDS实例创建完成
aws rds wait db-instance-available \
    --db-instance-identifier airline-order-db \
    --region us-east-2
```

### 2.4 获取RDS连接信息
```bash
# 获取RDS端点
aws rds describe-db-instances \
    --db-instance-identifier airline-order-db \
    --query 'DBInstances[0].Endpoint.Address' \
    --output text \
    --region us-east-2

# 获取完整连接信息
aws rds describe-db-instances \
    --db-instance-identifier airline-order-db \
    --query 'DBInstances[0].{Endpoint:Endpoint.Address,Port:Endpoint.Port,Status:DBInstanceStatus}' \
    --region us-east-2
```

## 📥 第三步：数据迁移

### 3.1 创建数据库和用户
```sql
-- 连接到RDS实例
mysql -h your-rds-endpoint.us-east-2.rds.amazonaws.com -P 3306 -u admin -p

-- 创建数据库
CREATE DATABASE IF NOT EXISTS airline_order_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 创建应用用户
CREATE USER 'airline_app'@'%' IDENTIFIED BY 'AirlineApp2024!';

-- 授权
GRANT ALL PRIVILEGES ON airline_order_db.* TO 'airline_app'@'%';
FLUSH PRIVILEGES;

-- 验证用户
SELECT User, Host FROM mysql.user WHERE User = 'airline_app';
```

### 3.2 导入数据结构
```bash
# 导入数据库结构
mysql -h your-rds-endpoint.us-east-2.rds.amazonaws.com \
      -P 3306 -u admin -p airline_order_db < airline_order_schema.sql

# 或者导入完整备份
mysql -h your-rds-endpoint.us-east-2.rds.amazonaws.com \
      -P 3306 -u admin -p airline_order_db < airline_order_backup.sql
```

### 3.3 验证数据迁移
```sql
-- 连接到RDS
mysql -h your-rds-endpoint.us-east-2.rds.amazonaws.com -P 3306 -u admin -p airline_order_db

-- 检查表结构
SHOW TABLES;

-- 检查数据量
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'airline_order_db';

-- 检查关键表数据
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM flights;
SELECT COUNT(*) FROM orders;
SELECT COUNT(*) FROM shedlock;
```

## ⚙️ 第四步：应用配置更新

### 4.1 更新Spring Boot配置

#### application-prod.properties
```properties
# AWS RDS数据库配置
spring.datasource.url=jdbc:mysql://your-rds-endpoint.us-east-2.rds.amazonaws.com:3306/airline_order_db?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=airline_app
spring.datasource.password=AirlineApp2024!

# 连接池配置（针对RDS优化）
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.leak-detection-threshold=60000

# JPA配置
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.jdbc.time_zone=UTC

# SSL配置
spring.datasource.hikari.data-source-properties.useSSL=true
spring.datasource.hikari.data-source-properties.requireSSL=true
spring.datasource.hikari.data-source-properties.verifyServerCertificate=false
```

### 4.2 环境变量配置
```bash
# 在EC2实例上设置环境变量
export DB_HOST=your-rds-endpoint.us-east-2.rds.amazonaws.com
export DB_PORT=3306
export DB_NAME=airline_order_db
export DB_USERNAME=airline_app
export DB_PASSWORD=AirlineApp2024!
export DB_SSL=true

# 或者在Docker Compose中配置
```

### 4.3 Docker Compose配置更新
```yaml
version: '3.8'
services:
  backend:
    image: ${ECR_URI}:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://${DB_HOST}:3306/${DB_NAME}?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
      - SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    restart: unless-stopped
    networks:
      - airline-network

networks:
  airline-network:
    driver: bridge
```

## 🧪 第五步：测试验证

### 5.1 连接测试
```bash
# 测试数据库连接
mysql -h your-rds-endpoint.us-east-2.rds.amazonaws.com \
      -P 3306 -u airline_app -p airline_order_db \
      -e "SELECT 'Connection successful' as status;"

# 测试应用连接
curl -f http://your-ec2-ip:8080/actuator/health
```

### 5.2 功能测试
```bash
# 测试用户注册
curl -X POST http://your-ec2-ip:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'

# 测试用户登录
curl -X POST http://your-ec2-ip:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 测试航班查询
curl -X GET http://your-ec2-ip:8080/api/flights
```

### 5.3 性能测试
```sql
-- 检查慢查询
SELECT * FROM information_schema.PROCESSLIST WHERE Time > 1;

-- 检查连接数
SHOW STATUS LIKE 'Threads_connected';
SHOW STATUS LIKE 'Max_used_connections';

-- 检查缓存命中率
SHOW STATUS LIKE 'Qcache_hits';
SHOW STATUS LIKE 'Qcache_inserts';
```

## 📊 第六步：监控和优化

### 6.1 CloudWatch监控配置
```bash
# 启用RDS性能洞察
aws rds modify-db-instance \
    --db-instance-identifier airline-order-db \
    --enable-performance-insights \
    --performance-insights-retention-period 7 \
    --region us-east-2
```

### 6.2 数据库优化
```sql
-- 创建索引优化查询性能
CREATE INDEX idx_flights_departure_date ON flights(departure_date);
CREATE INDEX idx_flights_origin_destination ON flights(origin_airport, destination_airport);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- 分析表统计信息
ANALYZE TABLE flights, orders, users, passengers;

-- 优化表
OPTIMIZE TABLE flights, orders, users, passengers;
```

### 6.3 备份策略
```bash
# 创建RDS快照
aws rds create-db-snapshot \
    --db-instance-identifier airline-order-db \
    --db-snapshot-identifier airline-order-snapshot-$(date +%Y%m%d) \
    --region us-east-2

# 设置自动备份
aws rds modify-db-instance \
    --db-instance-identifier airline-order-db \
    --backup-retention-period 30 \
    --preferred-backup-window "03:00-04:00" \
    --region us-east-2
```

## 🔒 第七步：安全配置

### 7.1 网络安全
```bash
# 更新安全组，移除不必要的访问
aws ec2 revoke-security-group-ingress \
    --group-id sg-xxxxxxxxx \
    --protocol tcp \
    --port 3306 \
    --cidr 0.0.0.0/0 \
    --region us-east-2

# 只允许EC2安全组访问
aws ec2 authorize-security-group-ingress \
    --group-id sg-xxxxxxxxx \
    --protocol tcp \
    --port 3306 \
    --source-group sg-yyyyyyyyy \
    --region us-east-2
```

### 7.2 数据加密
```sql
-- 启用SSL连接
ALTER USER 'airline_app'@'%' REQUIRE SSL;

-- 检查SSL状态
SHOW STATUS LIKE 'Ssl_cipher';
```

### 7.3 访问控制
```sql
-- 创建只读用户（用于报表）
CREATE USER 'airline_readonly'@'%' IDENTIFIED BY 'ReadOnly2024!';
GRANT SELECT ON airline_order_db.* TO 'airline_readonly'@'%';

-- 创建备份用户
CREATE USER 'airline_backup'@'%' IDENTIFIED BY 'Backup2024!';
GRANT SELECT, LOCK TABLES, SHOW VIEW ON airline_order_db.* TO 'airline_backup'@'%';
```

## 🚨 故障排除

### 常见问题1: 连接超时
```bash
# 检查安全组配置
aws ec2 describe-security-groups --group-ids sg-xxxxxxxxx --region us-east-2

# 检查网络ACL
aws ec2 describe-network-acls --region us-east-2

# 测试网络连通性
telnet your-rds-endpoint.us-east-2.rds.amazonaws.com 3306
```

### 常见问题2: SSL连接失败
```properties
# 在连接字符串中禁用SSL验证（仅开发环境）
spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/airline_order_db?useSSL=true&verifyServerCertificate=false&allowPublicKeyRetrieval=true
```

### 常见问题3: 字符编码问题
```sql
-- 检查字符集
SHOW VARIABLES LIKE 'character_set%';
SHOW VARIABLES LIKE 'collation%';

-- 修改字符集
ALTER DATABASE airline_order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 📈 成本优化

### 开发环境成本控制
- 使用db.t3.micro（免费套餐）
- 单AZ部署
- 最小存储空间
- 短备份保留期

### 生产环境成本优化
- 使用预留实例
- 启用存储自动扩展
- 合理设置备份保留期
- 监控使用情况

## 📋 迁移检查清单

- [ ] 备份原数据库
- [ ] 创建RDS实例
- [ ] 配置安全组
- [ ] 导入数据结构
- [ ] 导入数据
- [ ] 更新应用配置
- [ ] 测试数据库连接
- [ ] 测试应用功能
- [ ] 配置监控
- [ ] 设置备份策略
- [ ] 优化性能
- [ ] 安全加固
- [ ] 文档更新

这个指南涵盖了从本地/远程MySQL到AWS RDS的完整迁移过程！
