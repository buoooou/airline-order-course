# 订单状态历史定时任务 - ShedLock 分布式锁配置说明

## 概述

本项目使用 **ShedLock** 实现分布式锁，确保在集群环境中定时任务只被一个实例执行，避免重复处理和资源竞争。

## 配置详情

### 1. 依赖配置

在 `pom.xml` 中已添加以下依赖：

```xml
<!-- ShedLock 分布式锁 -->
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-spring</artifactId>
    <version>4.44.0</version>
</dependency>
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-provider-jdbc-template</artifactId>
    <version>4.44.0</version>
</dependency>
```

### 2. 数据库表结构

在 `init.sql` 中已创建 `shedlock` 表：

```sql
CREATE TABLE IF NOT EXISTS `shedlock` (
  `name` VARCHAR(64) NOT NULL,
  `lock_until` TIMESTAMP(3) NULL,
  `locked_at` TIMESTAMP(3) NULL,
  `locked_by` VARCHAR(255) NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3. 配置类

已创建 `ShedLockConfig.java` 配置类：

- 启用 `@EnableSchedulerLock` 注解
- 配置 `lockProvider` 使用 JDBC 模板和数据库时间
- 默认锁持有时间为 10 分钟

### 4. 定时任务实现

在 `OrderStateHistoryScheduler.java` 中：

#### 任务 1：查询失败状态转换记录

```java
@Scheduled(cron = "0 5/5 * * * *") // 每小时的5分、10分、15分...55分执行
@SchedulerLock(name = "queryFailedStateTransitions",
               lockAtLeastFor = "30s",
               lockAtMostFor = "50s")
public void queryFailedStateTransitions() {
    // 每小时的5分、10分、15分...55分查询失败的订单状态转换记录
}
```

#### 任务 2：分析失败趋势

```java
@Scheduled(cron = "0 0 * * * *") // 每小时的整点执行
@SchedulerLock(name = "analyzeFailedTransitions",
               lockAtLeastFor = "2m",
               lockAtMostFor = "4m")
public void analyzeFailedTransitions() {
    // 每小时的整点分析失败的订单状态转换趋势
}
```

## 参数说明

### @SchedulerLock 参数

- **name**: 锁的唯一标识符（必需）
- **lockAtLeastFor**: 锁的最短持有时间（防止任务执行时间过短导致锁频繁释放）
- **lockAtMostFor**: 锁的最长持有时间（防止任务异常导致锁无法释放）

### 锁持有时间计算

- **查询任务**: 30s-50s（适合短时间查询）
- **分析任务**: 2m-4m（适合较长时间的分析操作）

## 集群部署注意事项

### 1. 数据库配置

确保所有应用实例连接同一个数据库，因为锁信息存储在数据库中。

### 2. 时间同步

所有服务器时间必须同步，建议使用 NTP 服务确保时间一致性。

### 3. 锁冲突处理

- 如果某个实例获取锁失败，任务将不会执行
- 日志中会记录锁获取失败的信息
- 不会影响其他正常运行的实例

### 4. 故障恢复

- 如果持有锁的实例崩溃，锁会在 `lockAtMostFor` 时间后自动释放
- 其他实例可以正常获取锁并继续执行任务

## 监控与日志

### 日志输出

定时任务会输出以下日志信息：

```
开始查询订单状态转换失败记录...
发现 X 条订单状态转换失败记录
失败记录: ID=X, 订单ID=X, 从状态=X, 到状态=X, 错误信息=X, 创建时间=X
```

### 监控建议

- 监控 `shedlock` 表中的锁状态
- 关注日志中的失败记录数量和错误信息
- 设置告警监控任务执行频率

## 测试验证

### 单元测试

已创建 `OrderStateHistorySchedulerTest.java` 测试类：

- 验证定时任务的业务逻辑
- 使用 `@Mock` 和 `@InjectMocks` 进行依赖注入
- 使用 Mockito 进行方法调用验证



## 故障排查

### 常见问题

1. **锁表不存在**: 确保执行了包含 `shedlock` 表的初始化 SQL
2. **时间不同步**: 检查服务器时间同步配置
3. **锁冲突频繁**: 调整 `lockAtLeastFor` 和 `lockAtMostFor` 参数

### 调试建议

- 启用 ShedLock 的调试日志：`logging.level.net.javacrumbs.shedlock=DEBUG`
- 检查数据库连接配置
- 验证 `@EnableSchedulerLock` 注解是否正确启用
