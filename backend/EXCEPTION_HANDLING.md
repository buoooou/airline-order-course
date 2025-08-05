# 全局异常处理使用指南

## 概述

本项目已集成全局异常处理功能，统一处理各种异常并提供标准化的错误响应格式。

## 功能特性

- ✅ 统一异常处理
- ✅ 标准化错误响应格式
- ✅ 详细错误日志记录
- ✅ 自定义业务异常支持
- ✅ 参数验证异常处理
- ✅ 安全相关异常处理

## 异常类型

### 1. 自定义业务异常

#### BusinessException（业务异常）
```java
throw new BusinessException("业务操作失败");
```

#### ResourceNotFoundException（资源未找到异常）
```java
throw new ResourceNotFoundException("用户", "ID", 123L);
```

#### JwtException（JWT异常）
```java
throw new JwtException("JWT令牌无效");
```

### 2. Spring框架异常

- **BadCredentialsException** - 认证失败
- **UsernameNotFoundException** - 用户不存在
- **AccessDeniedException** - 权限不足
- **MethodArgumentNotValidException** - 参数验证失败
- **ConstraintViolationException** - 约束违反
- **DataIntegrityViolationException** - 数据完整性违反

### 3. 通用异常

- **Exception** - 所有未处理的异常

## 错误响应格式

所有异常都会返回统一的JSON格式：

```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "业务错误",
    "message": "具体的错误信息",
    "path": "/api/orders/123"
}
```

## HTTP状态码

| 异常类型 | HTTP状态码 | 说明 |
|----------|------------|------|
| BusinessException | 400 | 业务错误 |
| ResourceNotFoundException | 404 | 资源未找到 |
| JwtException | 401 | JWT令牌错误 |
| BadCredentialsException | 401 | 认证失败 |
| AccessDeniedException | 403 | 权限不足 |
| MethodArgumentNotValidException | 400 | 参数验证失败 |
| DataIntegrityViolationException | 409 | 数据冲突 |
| Exception | 500 | 服务器内部错误 |

## 测试接口

### 1. 测试业务异常
```bash
curl -X GET http://localhost:8080/exception-test/business-exception
```

响应示例：
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "业务错误",
    "message": "这是一个业务异常示例",
    "path": "uri=/exception-test/business-exception"
}
```

### 2. 测试资源未找到异常
```bash
curl -X GET http://localhost:8080/exception-test/resource-not-found
```

响应示例：
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 404,
    "error": "资源未找到",
    "message": "用户 未找到，ID: 999",
    "path": "uri=/exception-test/resource-not-found"
}
```

### 3. 测试参数验证异常
```bash
curl -X POST http://localhost:8080/exception-test/validation-exception \
  -H "Content-Type: application/json" \
  -d '{"name": ""}'
```

响应示例：
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 400,
    "error": "参数验证失败",
    "message": "请求参数不合法: {name=名称不能为空}",
    "path": "uri=/exception-test/validation-exception"
}
```

### 4. 测试JWT异常
```bash
curl -X GET http://localhost:8080/exception-test/jwt-exception
```

响应示例：
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 401,
    "error": "JWT令牌错误",
    "message": "JWT令牌无效或已过期",
    "path": "uri=/exception-test/jwt-exception"
}
```

### 5. 测试运行时异常
```bash
curl -X GET http://localhost:8080/exception-test/runtime-exception
```

响应示例：
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 500,
    "error": "服务器内部错误",
    "message": "系统发生未知错误，请联系管理员",
    "path": "uri=/exception-test/runtime-exception"
}
```

## 在业务代码中使用

### 1. 抛出业务异常
```java
@Service
public class OrderService {
    
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("订单", "ID", id));
        
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("订单已取消，无法操作");
        }
        
        return convertToDto(order);
    }
}
```

### 2. 参数验证
```java
@PostMapping("/orders")
public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    // 如果验证失败，会自动抛出MethodArgumentNotValidException
    Order order = orderService.createOrder(request);
    return ResponseEntity.ok(convertToDto(order));
}
```

### 3. 自定义验证注解
```java
public class CreateOrderRequest {
    @NotBlank(message = "订单号不能为空")
    @Size(min = 10, max = 20, message = "订单号长度必须在10-20个字符之间")
    private String orderNumber;
    
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
}
```

## 日志记录

全局异常处理器会自动记录所有异常的详细信息：

```java
logger.error("业务异常: {}", ex.getMessage(), ex);
```

日志包含：
- 异常类型
- 异常消息
- 完整的堆栈跟踪
- 请求路径
- 时间戳

## 最佳实践

### 1. 异常粒度
- 使用具体的异常类型而不是通用Exception
- 为不同的业务场景定义专门的异常类

### 2. 异常消息
- 提供清晰、有意义的错误消息
- 避免暴露敏感信息
- 使用中文错误消息提高用户体验

### 3. 日志记录
- 记录足够的调试信息
- 避免记录敏感数据
- 使用适当的日志级别

### 4. 错误响应
- 保持响应格式一致
- 提供有用的错误信息
- 包含必要的调试信息（开发环境）

## 配置说明

全局异常处理器通过`@RestControllerAdvice`注解自动生效，无需额外配置。

## 注意事项

1. **生产环境**：
   - 避免在错误消息中暴露敏感信息
   - 使用适当的日志级别
   - 考虑添加错误监控和告警

2. **开发环境**：
   - 启用详细的错误信息
   - 记录完整的堆栈跟踪
   - 提供调试接口

3. **性能考虑**：
   - 异常处理不应影响正常业务流程
   - 避免在异常处理中进行复杂操作
   - 合理使用异常缓存 