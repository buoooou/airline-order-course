# ShedLock 集成说明

## 概述

ShedLock 是一个用于分布式系统的调度任务锁定库，确保在分布式环境中定时任务只被执行一次。本项目中，我们将 ShedLock 集成到了 OrderServiceImpl 服务中，实现了订单相关的分布式定时任务。

## 集成步骤

### 1. 添加依赖

在 `pom.xml` 中添加以下依赖：

```xml
<!-- ShedLock for distributed task locking -->
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

### 2. 创建锁表

在 `schema.sql` 中创建 ShedLock 所需的锁表：

```sql
-- ShedLock锁表
CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) NOT NULL,
    lock_until TIMESTAMP(3) NOT NULL,
    locked_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    locked_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);
```

### 3. 配置 ShedLock

创建 `ShedLockConfig.java` 配置类：

```java
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M") // 默认锁定最多30分钟
public class ShedLockConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .withTableName("shedlock")
                        .withColumnNames(
                                JdbcTemplateLockProvider.ColumnNames.builder()
                                        .lockName("name")
                                        .lockUntil("lock_until")
                                        .lockedAt("locked_at")
                                        .lockedBy("locked_by")
                                        .build()
                        )
                        .build()
        );
    }
}
```

### 4. 在主类中启用定时任务

确保在 `AirlineOrderBackendApplication.java` 中添加 `@EnableScheduling` 注解：

```java
@SpringBootApplication
@EnableScheduling
public class AirlineOrderBackendApplication {
    // ...
}
```

### 5. 在 OrderServiceImpl 中实现定时任务

在 `OrderServiceImpl.java` 中添加定时任务方法：

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    // 现有服务方法...
    
    /**
     * 处理超时未支付的订单
     * 每5分钟执行一次，锁定至少4分钟，最多10分钟
     */
    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(name = "processUnpaidOrders", lockAtLeastFor = "PT4M", lockAtMostFor = "PT10M")
    @Transactional
    public void processUnpaidOrders() {
        log.info("开始处理超时未支付订单...");
        try {
            // 查找所有待支付状态且创建时间超过30分钟的订单
            List<Order> unpaidOrders = orderRepository.findByStatusAndCreationDateBefore(
                    OrderStatus.PENDING_PAYMENT,
                    LocalDateTime.now().minusMinutes(30)
            );
            
            log.info("找到{}个超时未支付订单", unpaidOrders.size());
            
            for (Order order : unpaidOrders) {
                log.info("取消超时未支付订单: {}", order.getOrderNumber());
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            }
            
            log.info("超时未支付订单处理完成");
        } catch (Exception e) {
            log.error("处理超时未支付订单时发生错误", e);
        }
    }

    /**
     * 处理出票中的订单
     * 每10分钟执行一次，锁定至少9分钟，最多15分钟
     */
    @Scheduled(cron = "0 */10 * * * *")
    @SchedulerLock(name = "processTicketingOrders", lockAtLeastFor = "PT9M", lockAtMostFor = "PT15M")
    @Transactional
    public void processTicketingOrders() {
        log.info("开始处理出票中订单...");
        try {
            // 查找所有出票中状态且创建时间超过1小时的订单
            List<Order> ticketingOrders = orderRepository.findByStatusAndCreationDateBefore(
                    OrderStatus.TICKETING_IN_PROGRESS,
                    LocalDateTime.now().minusHours(1)
            );
            
            log.info("找到{}个长时间处于出票中的订单", ticketingOrders.size());
            
            for (Order order : ticketingOrders) {
                log.info("将长时间出票中订单标记为出票失败: {}", order.getOrderNumber());
                order.setStatus(OrderStatus.TICKETING_FAILED);
                orderRepository.save(order);
                // 这里可以添加通知逻辑，如发送邮件或短信通知用户出票失败
            }
            
            log.info("出票中订单处理完成");
        } catch (Exception e) {
            log.error("处理出票中订单时发生错误", e);
        }
    }
}
```

### 6. 创建测试控制器

创建 `SchedulerTestController.java` 用于手动触发定时任务：

```java
@RestController
@RequestMapping("/api/admin/scheduler")
@RequiredArgsConstructor
public class SchedulerTestController {

    private final OrderServiceImpl orderService;

    /**
     * 手动触发处理超时未支付订单的任务
     * @return 响应实体
     */
    @PostMapping("/process-unpaid-orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> triggerProcessUnpaidOrders() {
        orderService.processUnpaidOrders();
        return ResponseEntity.ok("处理超时未支付订单的任务已触发");
    }

    /**
     * 手动触发处理出票中订单的任务
     * @return 响应实体
     */
    @PostMapping("/process-ticketing-orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> triggerProcessTicketingOrders() {
        orderService.processTicketingOrders();
        return ResponseEntity.ok("处理出票中订单的任务已触发");
    }
}
```

## 使用说明

### 锁定参数说明

- `@SchedulerLock(name = "lockName", lockAtLeastFor = "PT4M", lockAtMostFor = "PT10M")`
  - `name`: 锁的唯一名称，用于在分布式环境中标识任务
  - `lockAtLeastFor`: 即使任务完成，锁也会保持的最短时间（防止任务重复执行）
  - `lockAtMostFor`: 锁的最长持有时间（防止死锁）

### 已实现的定时任务

1. **处理超时未支付订单**
   - 每5分钟执行一次
   - 查找创建时间超过30分钟且状态为 `PENDING_PAYMENT` 的订单
   - 将这些订单状态更新为 `CANCELLED`

2. **处理出票中订单**
   - 每10分钟执行一次
   - 查找创建时间超过1小时且状态为 `TICKETING_IN_PROGRESS` 的订单
   - 将这些订单状态更新为 `TICKETING_FAILED`

## 注意事项

1. 确保数据库中已创建 `shedlock` 表
2. 在分布式环境中，所有服务实例应使用同一个数据库来存储锁信息
3. 定时任务方法应该是幂等的，即多次执行不会产生副作用
4. 锁的持有时间应根据任务的预期执行时间来设置，避免过长或过短
5. 在测试环境中，可以使用 `SchedulerTestController` 手动触发任务进行测试