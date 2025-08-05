# 出票系统Mock客户端测试指南

## 概述

本项目集成了Mock出票系统客户端，用于模拟航空公司出票系统的各种异常场景和网络问题，帮助测试系统的容错能力和异常处理机制。

## 功能特性

- ✅ 模拟正常出票流程
- ✅ 模拟各种异常场景
- ✅ 模拟网络延迟和超时
- ✅ 模拟系统维护和故障
- ✅ 模拟业务逻辑异常
- ✅ 随机失败测试

## 异常场景测试

### 1. 系统超时异常
```bash
curl -X POST http://localhost:8080/ticketing-test/timeout-exception
```
**触发条件**：订单号包含 "TIMEOUT"
**异常类型**：TicketingSystemException
**错误代码**：TICKETING_SYSTEM_TIMEOUT

### 2. 座位不足异常
```bash
curl -X POST http://localhost:8080/ticketing-test/no-seat-exception
```
**触发条件**：订单号包含 "NO_SEAT"
**异常类型**：业务异常（返回失败响应）
**错误代码**：NO_SEAT_AVAILABLE

### 3. 航班取消异常
```bash
curl -X POST http://localhost:8080/ticketing-test/flight-cancelled-exception
```
**触发条件**：订单号包含 "FLIGHT_CANCELLED"
**异常类型**：业务异常（返回失败响应）
**错误代码**：FLIGHT_CANCELLED

### 4. 乘客信息错误异常
```bash
curl -X POST http://localhost:8080/ticketing-test/invalid-passenger-exception
```
**触发条件**：订单号包含 "INVALID_PASSENGER"
**异常类型**：业务异常（返回失败响应）
**错误代码**：INVALID_PASSENGER_INFO

### 5. 系统维护异常
```bash
curl -X POST http://localhost:8080/ticketing-test/maintenance-exception
```
**触发条件**：订单号包含 "MAINTENANCE"
**异常类型**：TicketingSystemException
**错误代码**：SYSTEM_MAINTENANCE

### 6. 网络连接失败异常
```bash
curl -X POST http://localhost:8080/ticketing-test/network-error-exception
```
**触发条件**：订单号包含 "NETWORK_ERROR"
**异常类型**：TicketingSystemException
**错误代码**：NETWORK_ERROR

### 7. 随机失败测试
```bash
curl -X POST http://localhost:8080/ticketing-test/random-failure
```
**触发条件**：10%随机概率
**异常类型**：TicketingSystemException
**错误代码**：RANDOM_ERROR

## 其他功能测试

### 1. 正常出票
```bash
curl -X POST http://localhost:8080/ticketing-test/normal-ticketing
```

### 2. 查询出票状态
```bash
curl -X GET http://localhost:8080/ticketing-test/query-status/ORD-2024-001
```

### 3. 查询出票状态失败
```bash
curl -X GET http://localhost:8080/ticketing-test/query-status-failed
```

### 4. 取消出票
```bash
curl -X POST http://localhost:8080/ticketing-test/cancel-ticket
```

### 5. 取消出票失败
```bash
curl -X POST http://localhost:8080/ticketing-test/cancel-ticket-failed
```

### 6. 改签
```bash
curl -X POST http://localhost:8080/ticketing-test/change-ticket
```

### 7. 改签失败
```bash
curl -X POST http://localhost:8080/ticketing-test/change-ticket-failed
```

### 8. 座位可用性查询
```bash
curl -X GET http://localhost:8080/ticketing-test/seat-availability/CA1234/ECONOMY
```

### 9. 座位查询失败
```bash
curl -X GET http://localhost:8080/ticketing-test/seat-query-failed
```

### 10. 座位不足
```bash
curl -X GET http://localhost:8080/ticketing-test/seat-no-available
```

## 响应格式

### 成功响应
```json
{
    "ticketNumber": "TKT1705123456789",
    "orderNumber": "ORD-2024-001",
    "status": "SUCCESS",
    "message": "出票成功",
    "ticketingTime": "2024-01-15T10:30:00",
    "seatNumber": "A15",
    "gateNumber": "G8"
}
```

### 失败响应
```json
{
    "orderNumber": "ORD-NO_SEAT-001",
    "status": "FAILED",
    "errorCode": "NO_SEAT_AVAILABLE",
    "errorMessage": "所选座位类型已售罄",
    "ticketingTime": "2024-01-15T10:30:00"
}
```

### 异常响应
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 503,
    "error": "出票系统错误",
    "errorCode": "TICKETING_SYSTEM_TIMEOUT",
    "message": "出票系统超时，请稍后重试",
    "path": "uri=/ticketing-test/timeout-exception"
}
```

## 网络延迟模拟

Mock客户端会模拟100-500ms的网络延迟，更真实地反映实际网络环境。

## 使用场景

### 1. 单元测试
```java
@Test
public void testTicketingTimeout() {
    TicketingRequest request = new TicketingRequest();
    request.setOrderNumber("ORD-TIMEOUT-001");
    
    assertThrows(TicketingSystemException.class, () -> {
        ticketingClient.issueTicket(request);
    });
}
```

### 2. 集成测试
```java
@Test
public void testTicketingIntegration() {
    // 测试正常流程
    TicketingResponse response = ticketingClient.issueTicket(normalRequest);
    assertEquals("SUCCESS", response.getStatus());
    
    // 测试异常流程
    assertThrows(TicketingSystemException.class, () -> {
        ticketingClient.issueTicket(timeoutRequest);
    });
}
```

### 3. 压力测试
```java
@Test
public void testTicketingConcurrency() {
    // 并发测试出票系统
    List<CompletableFuture<TicketingResponse>> futures = new ArrayList<>();
    
    for (int i = 0; i < 100; i++) {
        futures.add(CompletableFuture.supplyAsync(() -> 
            ticketingClient.issueTicket(createRequest("ORD-" + i))
        ));
    }
    
    // 等待所有请求完成
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
}
```

## 监控和日志

Mock客户端会记录详细的日志信息，包括：
- 请求开始和结束时间
- 异常类型和错误代码
- 网络延迟模拟
- 业务逻辑处理过程

## 注意事项

1. **测试环境**：Mock客户端仅用于测试环境，生产环境应使用真实的出票系统
2. **异常处理**：确保系统能够正确处理各种异常场景
3. **超时配置**：根据实际网络环境调整超时配置
4. **重试机制**：考虑实现重试机制处理临时性故障
5. **降级策略**：实现降级策略处理系统不可用情况 