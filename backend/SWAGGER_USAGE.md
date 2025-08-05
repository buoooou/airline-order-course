# Swagger API文档使用指南

## 概述

本项目集成了SpringDoc OpenAPI 3，提供了完整的API文档和交互式测试界面。

## 访问地址

### Swagger UI界面
- **本地开发环境**: http://localhost:8080/swagger-ui.html
- **API文档JSON**: http://localhost:8080/api-docs

## 功能特性

### ✅ 完整的API文档
- 所有控制器接口的详细说明
- 请求参数和响应格式说明
- 错误码和状态码说明

### ✅ 交互式测试
- 直接在浏览器中测试API
- 支持JWT认证
- 实时响应查看

### ✅ 分组管理
- 按功能模块分组（订单管理、用户认证、出票系统测试等）
- 按字母顺序排序
- 清晰的标签分类

### ✅ 安全认证
- JWT Bearer Token认证
- 自动携带认证信息
- 权限验证说明

## API分组说明

### 1. 订单管理 (Order Management)
- **获取所有订单**: `GET /orders` - 需要ADMIN权限
- **根据ID获取订单**: `GET /orders/{id}` - 需要USER权限

### 2. 用户认证 (User Authentication)
- **用户登录**: `POST /auth/login`
- **用户注册**: `POST /auth/register`
- **认证服务测试**: `GET /auth/test`

### 3. 出票系统测试 (Ticketing System Test)
- **正常出票测试**: `POST /ticketing-test/normal-ticketing`
- **系统超时异常**: `POST /ticketing-test/timeout-exception`
- **座位不足异常**: `POST /ticketing-test/no-seat-exception`
- **航班取消异常**: `POST /ticketing-test/flight-cancelled-exception`
- **乘客信息错误**: `POST /ticketing-test/invalid-passenger-exception`
- **系统维护异常**: `POST /ticketing-test/maintenance-exception`
- **网络连接失败**: `POST /ticketing-test/network-error-exception`
- **随机失败测试**: `POST /ticketing-test/random-failure`

### 4. 其他测试接口
- **AOP演示**: `/aop-demo/**`
- **JWT测试**: `/jwt-test/**`
- **异常处理测试**: `/exception-test/**`
- **MapStruct测试**: `/mapstruct-test/**`
- **ShedLock测试**: `/shedlock-test/**`

## 使用步骤

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 访问Swagger UI
打开浏览器访问: http://localhost:8080/swagger-ui.html

### 3. 获取JWT Token
1. 展开"用户认证"分组
2. 点击"用户登录"接口
3. 点击"Try it out"
4. 输入登录信息：
```json
{
  "username": "admin",
  "password": "admin123"
}
```
5. 点击"Execute"
6. 复制返回的token

### 4. 设置认证
1. 点击页面右上角的"Authorize"按钮
2. 在Bearer Token框中输入：`Bearer {your-token}`
3. 点击"Authorize"

### 5. 测试API
1. 展开需要测试的API分组
2. 点击具体的API接口
3. 点击"Try it out"
4. 填写参数（如果需要）
5. 点击"Execute"
6. 查看响应结果

## 配置说明

### 应用配置 (application.yml)
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  packages-to-scan: com.position.airlineorderbackend.controller
```

### OpenAPI配置 (OpenApiConfig.java)
- API基本信息配置
- 服务器环境配置
- 安全认证配置
- 联系信息配置

## 注解说明

### 控制器级别注解
```java
@Tag(name = "订单管理", description = "订单相关的API接口")
@SecurityRequirement(name = "Bearer Authentication")
```

### 方法级别注解
```java
@Operation(summary = "获取所有订单", description = "获取系统中的所有订单列表")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "成功获取订单列表"),
    @ApiResponse(responseCode = "401", description = "未授权访问")
})
```

### 参数注解
```java
@Parameter(description = "订单ID") @PathVariable Long id
```

### DTO注解
```java
@Schema(description = "订单数据传输对象")
public class OrderDto {
    @Schema(description = "订单ID", example = "1")
    private Long id;
}
```

## 测试示例

### 1. 测试用户登录
```bash
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. 测试获取订单（需要认证）
```bash
curl -X GET "http://localhost:8080/orders/1" \
  -H "Authorization: Bearer {your-jwt-token}"
```

### 3. 测试出票系统异常
```bash
curl -X POST "http://localhost:8080/ticketing-test/timeout-exception"
```

## 注意事项

1. **认证要求**: 大部分API需要JWT认证，请先获取token
2. **权限控制**: 不同API需要不同权限级别
3. **错误处理**: 查看响应状态码和错误信息
4. **测试环境**: 出票系统测试接口会模拟各种异常场景
5. **数据安全**: 生产环境请修改JWT密钥

## 故障排除

### 1. 无法访问Swagger UI
- 检查应用是否正常启动
- 确认端口8080未被占用
- 检查防火墙设置

### 2. 认证失败
- 确认JWT token格式正确
- 检查token是否过期
- 验证用户权限

### 3. API调用失败
- 检查请求参数格式
- 确认Content-Type设置正确
- 查看服务器日志

## 扩展功能

### 自定义响应示例
```java
@ApiResponse(responseCode = "200", description = "成功", 
             content = @Content(schema = @Schema(implementation = OrderDto.class)))
```

### 添加更多服务器环境
```java
.servers(List.of(
    new Server().url("http://localhost:8080").description("本地开发环境"),
    new Server().url("https://staging.airline.com").description("测试环境"),
    new Server().url("https://api.airline.com").description("生产环境")
))
```

### 自定义安全方案
```java
.addSecuritySchemes("API Key", new SecurityScheme()
    .type(SecurityScheme.Type.APIKEY)
    .in(SecurityScheme.In.HEADER)
    .name("X-API-Key"))
``` 