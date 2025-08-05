# JWT认证使用指南

## 概述

本项目已集成JWT（JSON Web Token）认证系统，提供安全的用户认证和授权功能。

## 功能特性

- ✅ JWT令牌生成和验证
- ✅ 用户注册和登录
- ✅ 基于角色的权限控制
- ✅ 密码加密存储（BCrypt）
- ✅ 无状态认证（Stateless）

## API接口

### 1. 用户注册
```http
POST /auth/register
Content-Type: application/json

{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "role": "USER"
}
```

### 2. 用户登录
```http
POST /auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "password"
}
```

响应示例：
```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "username": "admin",
    "role": "ADMIN"
}
```

### 3. 使用JWT令牌访问受保护的接口

在请求头中添加：
```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 测试用户

系统预置了以下测试用户：

| 用户名 | 密码 | 角色 | 邮箱 |
|--------|------|------|------|
| admin | password | ADMIN | admin@airline.com |
| user1 | password | USER | user1@airline.com |
| user2 | password | USER | user2@airline.com |

## 权限控制

### 接口权限要求

1. **公开接口**（无需认证）：
   - `/auth/**` - 认证相关接口
   - `/aop-demo/**` - AOP演示接口
   - `/swagger-ui/**` - API文档

2. **需要认证的接口**：
   - `/orders/**` - 订单管理接口
   - `/jwt-test/authenticated` - 认证测试接口
   - `/jwt-test/current-user` - 获取当前用户信息

3. **需要ADMIN权限的接口**：
   - `/jwt-test/admin-only` - 管理员专用接口

4. **需要USER或ADMIN权限的接口**：
   - `/jwt-test/user-only` - 用户接口

## 测试步骤

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 用户登录
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

### 3. 使用令牌访问受保护接口
```bash
# 将返回的token替换到下面的命令中
curl -X GET http://localhost:8080/jwt-test/authenticated \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 4. 测试不同权限级别
```bash
# 测试管理员接口
curl -X GET http://localhost:8080/jwt-test/admin-only \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 测试用户接口
curl -X GET http://localhost:8080/jwt-test/user-only \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 配置说明

### JWT配置（application.yml）
```yaml
jwt:
  secret: your-secret-key-here-make-it-long-and-secure-in-production
  expiration: 86400000  # 24小时，单位：毫秒
```

### 安全配置
- 使用BCrypt加密密码
- 无状态会话管理
- JWT过滤器自动验证令牌
- 基于角色的访问控制

## 注意事项

1. **生产环境**：
   - 修改JWT密钥为强密钥
   - 设置合适的令牌过期时间
   - 启用HTTPS

2. **安全建议**：
   - 定期更换JWT密钥
   - 实现令牌刷新机制
   - 添加令牌黑名单功能

3. **错误处理**：
   - 无效令牌返回401
   - 权限不足返回403
   - 服务器错误返回500

## 故障排除

### 常见问题

1. **401 Unauthorized**：
   - 检查令牌是否正确
   - 确认令牌未过期
   - 验证令牌格式

2. **403 Forbidden**：
   - 检查用户角色权限
   - 确认接口访问权限

3. **500 Internal Server Error**：
   - 检查数据库连接
   - 查看应用日志 