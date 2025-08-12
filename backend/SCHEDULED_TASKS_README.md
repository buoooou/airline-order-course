# 航空订单系统 - 定时任务功能说明

## 概述

本系统使用 **ShedLock** 实现分布式定时任务，确保在多实例部署环境中，定时任务只在一个实例上执行，避免重复处理和数据不一致问题。

## 功能特性

### 🔒 分布式锁机制
- 使用 ShedLock 4.43.0 版本
- 基于数据库的锁实现（JDBC Template）
- 支持锁的自动过期和释放
- 防止任务重复执行

### ⏰ 定时任务类型

#### 1. 取消超时待支付订单
- **执行频率**: 每5分钟
- **锁定时间**: 最长4分钟，最短1分钟
- **功能**: 自动取消超过30分钟未支付的订单
- **配置项**: `app.order.payment-timeout-minutes=30`

#### 2. 处理超时出票订单
- **执行频率**: 每10分钟
- **锁定时间**: 最长9分钟，最短2分钟
- **功能**: 将超过60分钟的出票中订单标记为出票失败
- **配置项**: `app.order.ticketing-timeout-minutes=60`

#### 3. 取消长时间出票失败订单
- **执行频率**: 每小时
- **锁定时间**: 最长55分钟，最短5分钟
- **功能**: 自动取消出票失败超过24小时的订单
- **配置项**: `app.order.ticketing-failed-timeout-hours=24`

#### 4. 每日维护任务
- **执行频率**: 每天凌晨2点
- **锁定时间**: 最长2小时，最短10分钟
- **功能**: 生成每日订单报告，清理过期数据

## 数据库表结构

### ShedLock表 (`shedlock`)
```sql
CREATE TABLE `shedlock` (
  `name` varchar(64) NOT NULL COMMENT '锁名称',
  `lock_until` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '锁定到期时间',
  `locked_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '锁定时间',
  `locked_by` varchar(255) NOT NULL COMMENT '锁定者标识',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ShedLock分布式锁表';
```

## 配置说明

### application.properties 配置项

```properties
# 订单超时配置
app.order.payment-timeout-minutes=30
app.order.ticketing-timeout-minutes=60
app.order.ticketing-failed-timeout-hours=24

# 定时任务开关
app.scheduled.enabled=true

# ShedLock配置
app.shedlock.default-lock-at-most-for=10m
app.shedlock.table-name=shedlock
```

## API接口

### 管理员专用接口 (`/api/admin/scheduled-tasks`)

#### 1. 获取定时任务配置
```http
GET /api/admin/scheduled-tasks/config
Authorization: Bearer {admin_token}
```

#### 2. 手动触发任务
```http
# 取消超时待支付订单
POST /api/admin/scheduled-tasks/cancel-timeout-payment-orders

# 处理超时出票订单
POST /api/admin/scheduled-tasks/handle-timeout-ticketing-orders

# 取消长时间出票失败订单
POST /api/admin/scheduled-tasks/cancel-long-time-failed-orders

# 执行每日维护任务
POST /api/admin/scheduled-tasks/daily-maintenance
```

#### 3. 获取统计信息
```http
GET /api/admin/scheduled-tasks/statistics
GET /api/admin/scheduled-tasks/health
```

## 部署注意事项

### 1. 多实例部署
- 所有实例共享同一个数据库
- ShedLock 自动协调任务执行
- 只有一个实例会执行定时任务

### 2. 时区设置
- 确保所有实例使用相同的时区
- 建议使用 UTC 时区避免夏令时问题

### 3. 数据库连接
- 确保数据库连接池配置合理
- 定时任务执行时会占用数据库连接

### 4. 日志监控
- 定时任务执行情况会记录在日志中
- 建议配置日志聚合和监控

## 监控和运维

### 1. 日志关键字
- `开始执行定时任务`
- `定时任务完成`
- `自动取消超时订单`
- `自动标记超时出票订单为失败`

### 2. 监控指标
- 定时任务执行频率
- 处理的订单数量
- 任务执行耗时
- 异常发生次数

### 3. 故障排查
- 检查 ShedLock 表中的锁记录
- 查看应用日志中的异常信息
- 验证数据库连接状态
- 确认定时任务配置是否正确

## 性能优化建议

### 1. 批量处理
- 每次最多处理1000个订单
- 避免一次性处理过多数据

### 2. 索引优化
- 在订单表的状态和创建时间字段上建立索引
- 定期分析查询性能

### 3. 锁时间调优
- 根据实际处理时间调整锁定时间
- 避免锁时间过长影响其他实例

## 扩展功能

### 1. 自定义定时任务
可以通过实现新的定时任务方法来扩展功能：

```java
@Scheduled(fixedRate = 60000) // 1分钟
@SchedulerLock(name = "customTask", lockAtMostFor = "50s")
public void customTask() {
    // 自定义任务逻辑
}
```

### 2. 动态配置
可以通过配置中心动态调整定时任务参数，无需重启应用。

### 3. 任务监控
可以集成 Spring Boot Actuator 或其他监控工具来监控定时任务的执行状态。

## 常见问题

### Q1: 定时任务没有执行怎么办？
- 检查 `app.scheduled.enabled` 配置
- 确认 ShedLock 表是否存在
- 查看应用日志中的错误信息

### Q2: 多个实例都在执行定时任务？
- 检查所有实例是否连接到同一个数据库
- 确认 ShedLock 配置是否正确
- 查看 ShedLock 表中的锁记录

### Q3: 定时任务执行时间过长？
- 调整锁定时间配置
- 优化任务处理逻辑
- 考虑分批处理大量数据

### Q4: 如何临时禁用定时任务？
- 设置 `app.scheduled.enabled=false`
- 重启应用生效

## 版本历史

- **v1.0.0**: 初始版本，支持基本的订单超时处理
- **v1.1.0**: 添加管理接口和手动触发功能
- **v1.2.0**: 增加统计和监控功能

---

**注意**: 本功能需要管理员权限才能访问相关接口，请确保在生产环境中正确配置权限控制。
