# 航空订单管理系统 API 测试指南

## 1. 环境准备

### 1.1 应用状态检查
```bash
# 检查应用是否运行
curl -s http://localhost:8080/actuator/health

# 检查端口占用
netstat -ano | findstr :8080
```

### 1.2 Swagger UI 文档
访问：http://localhost:8080/swagger-ui/index.html

**如果遇到 "Failed to load remote configuration" 错误：**

1. **检查API文档端点**：
   ```bash
   curl -s http://localhost:8080/v3/api-docs
   ```

2. **检查Swagger UI资源**：
   ```bash
   curl -s http://localhost:8080/swagger-ui/index.html
   ```

3. **替代访问方式**：
   - 主页面：http://localhost:8080/swagger-ui/index.html
   - 备用页面：http://localhost:8080/swagger-ui.html
   - API文档：http://localhost:8080/v3/api-docs

4. **常见解决方案**：
   - 清除浏览器缓存
   - 使用无痕模式访问
   - 检查浏览器控制台错误信息
   - 确认应用已完全启动

## 2. 认证相关 API 测试

### 2.1 用户注册

**PowerShell 兼容版本：**
```powershell
# PowerShell 单行版本
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{"username": "test123", "email": "test123@test.com", "password": "test123", "role": "user"}'

# PowerShell 多行版本（使用反引号）
curl -X POST http://localhost:8080/auth/register `
  -H "Content-Type: application/json" `
  -d '{"username": "test123", "email": "test123@test.com", "password": "test123", "role": "user"}'

# PowerShell 使用变量版本
$body = @{
    username = "test123"
    email = "test123@test.com"
    password = "test123"
    role = "user"
} | ConvertTo-Json

curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d $body
```

**Git Bash / WSL 版本：**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
  "username": "test123",
  "email": "test123@test.com",
  "password": "test123",
  "role": "user"
}'
```

**预期响应：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "test123",
  "message": "注册成功"
}
```

### 2.2 用户登录

**PowerShell 兼容版本：**
```powershell
# PowerShell 单行版本（使用默认用户）
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"username": "admin", "password": "password"}'

# PowerShell 多行版本（使用反引号）
curl -X POST http://localhost:8080/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username": "admin", "password": "password"}'

# PowerShell 使用变量版本
$loginBody = @{
    username = "admin"
    password = "password"
} | ConvertTo-Json

curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d $loginBody
```

**Git Bash / WSL 版本：**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
  "username": "admin",
  "password": "password"
}'
```

**默认用户信息：**
- **管理员用户**：`admin` / `password`
- **普通用户**：`user` / `password`

**预期响应：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "message": "登录成功"
}
```

### 2.3 认证服务测试
```bash
curl -X GET http://localhost:8080/auth/test
```

## 3. 订单管理 API 测试

### 3.1 获取所有订单（需要ADMIN权限）
```bash
# 首先获取JWT token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' | jq -r '.token')

# 使用token获取所有订单
curl -X GET http://localhost:8080/orders \
  -H "Authorization: Bearer $TOKEN"
```

### 3.2 根据ID获取订单（需要USER权限）
```bash
curl -X GET http://localhost:8080/orders/1 \
  -H "Authorization: Bearer $TOKEN"
```

## 4. 订单操作 API 测试

### 4.1 支付订单
```bash
curl -X POST http://localhost:8080/orders/1/actions/pay \
  -H "Authorization: Bearer $TOKEN"
```

### 4.2 开始出票
```bash
curl -X POST http://localhost:8080/orders/1/actions/start-ticketing \
  -H "Authorization: Bearer $TOKEN"
```

### 4.3 完成出票
```bash
curl -X POST http://localhost:8080/orders/1/actions/complete-ticketing \
  -H "Authorization: Bearer $TOKEN"
```

### 4.4 出票失败
```bash
curl -X POST http://localhost:8080/orders/1/actions/fail-ticketing \
  -H "Authorization: Bearer $TOKEN"
```

### 4.5 取消订单
```bash
curl -X POST http://localhost:8080/orders/1/actions/cancel \
  -H "Authorization: Bearer $TOKEN"
```

### 4.6 重新支付
```bash
curl -X POST http://localhost:8080/orders/1/actions/retry-payment \
  -H "Authorization: Bearer $TOKEN"
```

### 4.7 重新出票
```bash
curl -X POST http://localhost:8080/orders/1/actions/retry-ticketing \
  -H "Authorization: Bearer $TOKEN"
```

## 5. 定时任务 API 测试

### 5.1 手动触发超时订单检查
```bash
curl -X POST http://localhost:8080/scheduler/check-timeout-orders \
  -H "Authorization: Bearer $TOKEN"
```

### 5.2 手动触发出票失败重试
```bash
curl -X POST http://localhost:8080/scheduler/retry-failed-ticketing \
  -H "Authorization: Bearer $TOKEN"
```

### 5.3 手动触发订单状态统计
```bash
curl -X POST http://localhost:8080/scheduler/order-statistics \
  -H "Authorization: Bearer $TOKEN"
```

### 5.4 手动触发长时间出票订单检查
```bash
curl -X POST http://localhost:8080/scheduler/check-stuck-ticketing \
  -H "Authorization: Bearer $TOKEN"
```

### 5.5 获取定时任务状态
```bash
curl -X GET http://localhost:8080/scheduler/status \
  -H "Authorization: Bearer $TOKEN"
```

## 6. 测试工具 API

### 6.1 JWT测试
```bash
curl -X GET http://localhost:8080/jwt/test \
  -H "Authorization: Bearer $TOKEN"
```

### 6.2 MapStruct测试
```bash
curl -X GET http://localhost:8080/mapstruct/test
```

### 6.3 ShedLock测试
```bash
curl -X GET http://localhost:8080/shedlock/test
```

### 6.4 异常测试
```bash
# 测试各种异常情况
curl -X GET http://localhost:8080/exception/test
curl -X POST http://localhost:8080/exception/test \
  -H "Content-Type: application/json" \
  -d '{"testType": "validation"}'
```

### 6.5 出票系统测试
```bash
curl -X GET http://localhost:8080/ticketing/test
```

## 7. 使用 Postman 测试

### 7.1 导入集合
1. 打开 Postman
2. 点击 "Import" 按钮
3. 选择 "Raw text" 选项卡
4. 粘贴以下JSON：

```json
{
  "info": {
    "name": "航空订单管理系统 API",
    "description": "航空订单管理系统的完整API测试集合",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    },
    {
      "key": "token",
      "value": ""
    }
  ],
  "item": [
    {
      "name": "认证",
      "item": [
        {
          "name": "用户注册",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"testuser\",\n  \"password\": \"password123\",\n  \"email\": \"test@example.com\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/auth/register",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "register"]
            }
          }
        },
        {
          "name": "用户登录",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"testuser\",\n  \"password\": \"password123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "login"]
            }
          }
        }
      ]
    }
  ]
}
```

## 8. 测试脚本

### 8.1 Windows PowerShell 测试脚本
```powershell
# 设置基础URL
$baseUrl = "http://localhost:8080"

# 1. 测试认证服务
Write-Host "=== 测试认证服务 ==="
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"admin123"}'
$token = $loginResponse.token
Write-Host "登录成功，Token: $token"

# 2. 测试获取订单
Write-Host "`n=== 测试获取订单 ==="
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}
$orders = Invoke-RestMethod -Uri "$baseUrl/orders" -Method GET -Headers $headers
Write-Host "获取到 $($orders.Count) 个订单"

# 3. 测试定时任务
Write-Host "`n=== 测试定时任务 ==="
$schedulerStatus = Invoke-RestMethod -Uri "$baseUrl/scheduler/status" -Method GET -Headers $headers
Write-Host "定时任务状态: $schedulerStatus"
```

### 8.2 Linux/Mac Bash 测试脚本
```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== 测试认证服务 ==="
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "登录成功，Token: $TOKEN"

echo -e "\n=== 测试获取订单 ==="
ORDERS=$(curl -s -X GET "$BASE_URL/orders" \
  -H "Authorization: Bearer $TOKEN")
echo "获取到订单: $ORDERS"

echo -e "\n=== 测试定时任务 ==="
SCHEDULER_STATUS=$(curl -s -X GET "$BASE_URL/scheduler/status" \
  -H "Authorization: Bearer $TOKEN")
echo "定时任务状态: $SCHEDULER_STATUS"
```

## 9. 常见问题排查

### 9.1 认证失败
- 检查用户名和密码是否正确
- 确认JWT token格式：`Bearer <token>`
- 检查token是否过期

### 9.2 权限不足
- 确认用户角色（USER/ADMIN）
- 检查API是否需要特定权限

### 9.3 连接失败
- 确认应用是否启动：`netstat -ano | findstr :8080`
- 检查防火墙设置
- 确认端口8080未被占用

### 9.4 数据库连接问题
- 确认MySQL服务是否运行
- 检查数据库连接配置
- 确认数据库表是否存在

## 10. 性能测试

### 10.1 使用 Apache Bench (ab)
```bash
# 测试获取订单接口性能
ab -n 100 -c 10 -H "Authorization: Bearer $TOKEN" http://localhost:8080/orders

# 测试登录接口性能
ab -n 100 -c 10 -p login_data.json -T application/json http://localhost:8080/auth/login
```

### 10.2 使用 JMeter
1. 下载并安装 JMeter
2. 创建测试计划
3. 添加线程组
4. 配置HTTP请求
5. 添加断言和监听器

## 11. 监控和日志

### 11.1 查看应用日志
```bash
# 如果使用Maven运行
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.position.airlineorderbackend=DEBUG"

# 查看实时日志
tail -f logs/application.log
```

### 11.2 健康检查
```bash
curl -s http://localhost:8080/actuator/health | jq
```

## 12. 测试报告

建议使用以下工具生成测试报告：
- Postman Collections
- Newman (Postman CLI)
- JMeter HTML报告
- 自定义测试脚本输出

---

**注意：** 请根据实际环境调整URL、用户名、密码等参数。测试前请确保应用正常运行且数据库连接正常。 