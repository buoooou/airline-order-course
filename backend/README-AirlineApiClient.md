# 航空公司API客户端模拟服务

## 概述

本项目实现了一个模拟的航空公司API客户端，用于在开发和测试环境中模拟与航空公司系统的交互。该模拟服务提供了出票、查询票务状态和退票等功能，并在OrderServiceImpl中集成了这些功能。作为高并发航空订单系统的重要组成部分，该客户端支持异步处理、重试机制和完整的错误处理。

## 实现内容

### 1. 航空公司API客户端 (AirlineApiClient)

位于`com.postion.airlineorderbackend.adapter`包中，提供以下功能：

- **出票 (issueTicket)**：模拟调用航空公司出票接口，有85%的成功率
- **查询票务状态 (queryTicketStatus)**：模拟查询票号状态，返回VALID、INVALID或REFUNDED
- **退票 (refundTicket)**：模拟申请退票，有90%的成功率
- **支持异步处理**：模拟真实环境的网络延迟和异步响应
- **内置重试机制**：支持失败重试，提高系统可靠性

### 2. 订单服务集成 (OrderServiceImpl)

在`OrderServiceImpl`中深度集成了航空公司API客户端，主要特性包括：

- **异步出票处理**：使用@Async注解实现异步出票，避免阻塞主线程
- **完整的错误处理**：捕获并处理所有可能的异常情况
- **状态同步机制**：根据API响应结果自动更新订单状态
- **日志记录**：详细记录每次API调用的请求和响应信息
- **性能监控**：记录API调用耗时，支持性能分析

### 3. 单元测试覆盖

项目提供了完善的单元测试，确保API客户端的可靠性：

#### AirlineApiClientTest 测试类

位于`src/test/java/com/postion/airlineorderbackend/adapter/`目录，包含：

- **出票功能测试**：测试成功和失败场景
- **状态查询测试**：验证各种票务状态的返回
- **退票功能测试**：测试退票成功和失败的处理
- **异常处理测试**：验证网络异常和超时情况的处理
- **Mock测试**：使用Mockito模拟RestTemplate，避免真实网络调用

#### 测试用例示例

```java
@Test
void issueTicket_Success() {
    // 测试成功出票场景
    when(restTemplate.postForEntity(any(), any(), eq(AirlineApiResponse.class)))
        .thenReturn(ResponseEntity.ok(successResponse));
    
    AirlineApiResponse response = airlineApiClient.issueTicket(orderId);
    assertTrue(response.isSuccess());
    assertNotNull(response.getTicketNumber());
}

@Test
void issueTicket_Failure() {
    // 测试出票失败场景
    when(restTemplate.postForEntity(any(), any(), eq(AirlineApiResponse.class)))
        .thenReturn(ResponseEntity.ok(failureResponse));
    
    AirlineApiResponse response = airlineApiClient.issueTicket(orderId);
    assertFalse(response.isSuccess());
    assertNull(response.getTicketNumber());
}
```

### 4. 集成测试支持

通过`OrderControllerIntegrationTest`提供完整的集成测试：

- **端到端测试**：从订单创建到出票完成的完整流程测试
- **并发测试**：验证高并发场景下的API调用稳定性
- **错误恢复测试**：测试系统在API调用失败后的恢复能力

## 使用方法

### 1. 标准业务流程

正常的业务流程通过订单服务自动调用航空公司API：

1. **创建订单** (OrderStatus.PENDING_PAYMENT)
2. **支付订单** (OrderStatus.PAID)
3. **请求出票** (OrderStatus.TICKETING_IN_PROGRESS)
4. **系统异步调用** 航空公司API出票
5. **状态更新** (OrderStatus.TICKETED 或 OrderStatus.TICKETING_FAILED)

### 2. 测试执行

#### 运行单元测试

```bash
# 运行所有测试
mvn test

# 只运行AirlineApiClient相关测试
mvn test -Dtest=AirlineApiClientTest

# 运行集成测试
mvn test -Dtest=OrderControllerIntegrationTest
```

#### 使用SpringDoc文档

项目集成了SpringDoc，可以通过以下方式查看API文档：
- 访问 http://localhost:8080/swagger-ui.html 查看交互式文档
- 访问 http://localhost:8080/v3/api-docs 查看原始JSON文档

## 技术特性

### 1. 高并发支持
- **连接池管理**：自动管理HTTP连接池
- **超时设置**：合理的连接和读取超时配置
- **重试机制**：支持指数退避重试策略

### 2. 监控和日志
- **调用日志**：记录每次API调用的详细信息
- **性能指标**：记录响应时间和成功率
- **错误追踪**：完整的异常堆栈和错误信息

### 3. 配置管理
- **外部化配置**：所有API端点和超时参数可配置
- **环境适配**：支持不同环境的配置切换
- **动态调整**：支持运行时参数调整

## 配置示例

### application.yml配置

```yaml
airline-api:
  base-url: http://mock-airline-api.com
  timeout:
    connect: 5000
    read: 10000
  retry:
    max-attempts: 3
    backoff-delay: 1000
```

## 注意事项

1. **测试环境专用**：这是一个模拟服务，仅用于开发和测试环境
2. **随机延迟和失败**：模拟服务引入了随机延迟(100-500ms)和随机失败(15-30%)，以模拟真实环境
3. **数据一致性**：测试数据不会持久化，重启服务后数据会重置
4. **并发限制**：模拟服务支持最大100个并发请求
5. **性能测试**：建议在压测前调整失败率和延迟参数
6. **生产迁移**：实际生产环境需要替换为真实的航空公司API端点

## 未来改进

- [ ] 支持更多航空公司API标准
- [ ] 添加API调用限流功能
- [ ] 实现调用结果缓存机制
- [ ] 添加API版本管理
- [ ] 支持批量出票操作