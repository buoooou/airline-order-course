# 航空订单管理系统 - 后端服务

## 项目简介

这是一个基于Spring Boot开发的航空订单管理系统后端服务，提供完整的用户认证、航班管理、订单管理等功能。系统采用RESTful API设计，支持JWT令牌认证，具备完善的权限控制和状态机管理。

## 技术栈

- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA + Hibernate
- **安全**: Spring Security + JWT
- **定时任务**: Spring Scheduler + ShedLock 4.43.0
- **文档**: Swagger/OpenAPI 3.0
- **构建工具**: Maven 3.6+
- **Java版本**: JDK 1.8+

## 核心功能

### 1. 用户认证系统
- 用户注册与登录
- JWT令牌生成与验证
- 令牌刷新机制
- 用户权限管理（ADMIN/USER）
- 密码BCrypt加密

### 2. 航班管理系统
- 航班CRUD操作
- 航班状态管理（正常/取消/延误）
- 座位管理（总座位数/可用座位数）
- 航班搜索与筛选
- 可预订航班查询

### 3. 订单管理系统
- 订单创建与支付
- 订单状态机管理
- 订单查询与详情
- 我的订单列表（分页）
- 订单状态更新

### 4. 定时任务管理系统
- ShedLock分布式定时任务
- 订单超时处理
- 系统状态监控
- 任务执行历史记录
- 手动任务触发

### 5. 状态机设计

#### 订单状态流转
```
PENDING_PAYMENT (待支付)
    ↓ pay()
PAID (已支付)
    ↓ startTicketing()
TICKETING_IN_PROGRESS (出票中)
    ↓ completeTicketing() / failTicketing()
TICKETED (已出票) / TICKETING_FAILED (出票失败)
    
任何状态都可以 → CANCELLED (已取消)
```

#### 航班状态
- `ACTIVE`: 正常运营，可预订
- `CANCELLED`: 已取消
- `DELAYED`: 延误

## 数据库设计

### 核心表结构

#### 用户表 (app_users_qiaozhe)
```sql
CREATE TABLE `app_users_qiaozhe` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
);
```

#### 航班信息表 (flight_info_qiaozhe)
```sql
CREATE TABLE `flight_info_qiaozhe` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `flight_number` varchar(10) NOT NULL,
  `airline` varchar(100) NOT NULL,
  `departure_airport` varchar(10) NOT NULL,
  `arrival_airport` varchar(10) NOT NULL,
  `departure_time` datetime(6) NOT NULL,
  `arrival_time` datetime(6) NOT NULL,
  `aircraft_type` varchar(50) NOT NULL,
  `total_seats` int NOT NULL,
  `available_seats` int NOT NULL,
  `price` decimal(19,2) NOT NULL,
  `status` enum('ACTIVE','CANCELLED','DELAYED') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
);
```

#### 订单表 (orders_qiaozhe)
```sql
CREATE TABLE `orders_qiaozhe` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_number` varchar(255) NOT NULL,
  `status` enum('PENDING_PAYMENT','PAID','TICKETING_IN_PROGRESS','TICKETING_FAILED','TICKETED','CANCELLED') NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `creation_date` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  `flight_info_id` bigint NOT NULL,
  `passenger_count` int NOT NULL,
  `passenger_names` varchar(500),
  `contact_phone` varchar(11),
  `contact_email` varchar(100),
  `payment_time` datetime(6),
  `ticketing_time` datetime(6),
  `cancellation_time` datetime(6),
  `cancellation_reason` varchar(500),
  `remarks` varchar(1000),
  PRIMARY KEY (`id`),
  KEY `fk_orders_user_id` (`user_id`),
  KEY `fk_orders_flight_id` (`flight_info_id`),
  CONSTRAINT `fk_orders_user_new` FOREIGN KEY (`user_id`) REFERENCES `app_users_qiaozhe` (`id`),
  CONSTRAINT `fk_orders_flight_new` FOREIGN KEY (`flight_info_id`) REFERENCES `flight_info_qiaozhe` (`id`)
);
```

#### ShedLock分布式锁表 (shedlock)
```sql
CREATE TABLE `shedlock` (
  `name` varchar(64) NOT NULL COMMENT '锁名称',
  `lock_until` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '锁定到期时间',
  `locked_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '锁定时间',
  `locked_by` varchar(255) NOT NULL COMMENT '锁定者标识',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ShedLock分布式锁表';
```

## 环境配置

### 数据库配置
```properties
# 数据库连接配置
spring.datasource.url=jdbc:mysql://18.207.201.92:3306/airline_order_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=airlineTest1234

# JPA配置
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# JWT配置
jwt.secret=63ffbc2b8d13ad5180ed7ae7c67f18c85d86046732fc9ced6a02a9d50abb1a03
jwt.expiration.ms=86400000

# Swagger配置
springdoc.swagger-ui.path=/swagger-ui.html

# 定时任务配置
app.order.payment-timeout-minutes=30
app.order.ticketing-timeout-minutes=60
app.order.ticketing-failed-timeout-hours=24
app.scheduled.enabled=true

# ShedLock配置
app.shedlock.default-lock-at-most-for=10m
app.shedlock.table-name=shedlock
```

## 快速开始

### 1. 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 2. 克隆项目
```bash
git clone <repository-url>
cd airline-order-course/backend
```

### 3. 配置数据库
确保MySQL服务运行，并创建数据库：
```sql
CREATE DATABASE airline_order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 启动应用
```bash
# 使用Maven启动
mvn spring-boot:run

# 或者先编译再运行
mvn clean package
java -jar target/airline-order-backend-0.0.1-SNAPSHOT.jar
```

### 5. 验证启动
- 应用启动后访问: http://localhost:8080
- Swagger文档: http://localhost:8080/swagger-ui.html
- 健康检查: http://localhost:8080/actuator/health

## API文档

### 认证相关 API

#### 用户注册
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "role": "ADMIN"
}
```

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### 获取当前用户信息
```http
GET /api/auth/me
Authorization: Bearer <jwt-token>
```

#### 刷新令牌
```http
POST /api/auth/refresh
Authorization: Bearer <jwt-token>
```

#### 用户登出
```http
POST /api/auth/logout
Authorization: Bearer <jwt-token>
```

### 航班管理 API

#### 获取航班列表
```http
GET /api/flights
```

#### 获取航班详情
```http
GET /api/flights/{id}
```

#### 创建航班 (需要ADMIN权限)
```http
POST /api/flights
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
  "flightNumber": "CA1234",
  "airline": "中国国际航空",
  "departureAirport": "PEK",
  "arrivalAirport": "SHA",
  "departureTime": "2025-07-30T08:00:00",
  "arrivalTime": "2025-07-30T10:30:00",
  "aircraftType": "A320",
  "totalSeats": 180,
  "availableSeats": 150,
  "price": 800.00,
  "status": "ACTIVE"
}
```

#### 更新航班 (需要ADMIN权限)
```http
PUT /api/flights/{id}
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
  "flightNumber": "CA1234",
  "airline": "中国国际航空",
  "departureAirport": "PEK",
  "arrivalAirport": "SHA",
  "departureTime": "2025-07-30T09:00:00",
  "arrivalTime": "2025-07-30T11:30:00",
  "aircraftType": "A320",
  "totalSeats": 180,
  "availableSeats": 148,
  "price": 850.00,
  "status": "ACTIVE"
}
```

#### 获取可预订航班
```http
GET /api/flights/bookable
```

#### 搜索航班
```http
GET /api/flights/search?departureAirport=PEK&arrivalAirport=SHA&departureDate=2025-07-30
```

### 订单管理 API

#### 创建订单
```http
POST /api/orders
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "userId": 3,
  "flightInfoId": 1,
  "passengerCount": 2,
  "passengerNames": ["张三", "李四"],
  "contactPhone": "13800138000",
  "contactEmail": "test@example.com",
  "remarks": "测试订单"
}
```

#### 支付订单
```http
PUT /api/orders/{id}/pay
Authorization: Bearer <jwt-token>
```

#### 获取我的订单
```http
GET /api/orders/my?page=0&size=10
Authorization: Bearer <jwt-token>
```

#### 获取订单详情
```http
GET /api/orders/{id}
Authorization: Bearer <jwt-token>
```

#### 更新订单状态 (需要ADMIN权限)
```http
PUT /api/orders/{id}/status
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
  "status": "TICKETING_IN_PROGRESS"
}
```

### 定时任务管理 API (需要ADMIN权限)

#### 获取定时任务配置
```http
GET /api/admin/scheduled-tasks/config
Authorization: Bearer <admin-jwt-token>
```

#### 获取系统健康状态
```http
GET /api/admin/scheduled-tasks/health
Authorization: Bearer <admin-jwt-token>
```

#### 获取定时任务统计
```http
GET /api/admin/scheduled-tasks/statistics
Authorization: Bearer <admin-jwt-token>
```

#### 手动触发取消超时待支付订单任务
```http
POST /api/admin/scheduled-tasks/cancel-timeout-payment-orders
Authorization: Bearer <admin-jwt-token>
```

#### 手动触发处理超时出票订单任务
```http
POST /api/admin/scheduled-tasks/handle-timeout-ticketing-orders
Authorization: Bearer <admin-jwt-token>
```

#### 手动触发取消长时间出票失败订单任务
```http
POST /api/admin/scheduled-tasks/cancel-long-time-failed-orders
Authorization: Bearer <admin-jwt-token>
```

#### 手动触发每日维护任务
```http
POST /api/admin/scheduled-tasks/daily-maintenance
Authorization: Bearer <admin-jwt-token>
```

## 项目结构

```
src/main/java/com/postion/airlineorderbackend/
├── config/                 # 配置类
│   ├── SecurityConfig.java     # Spring Security配置
│   ├── JwtRequestFilter.java   # JWT请求过滤器
│   └── ShedLockConfig.java     # ShedLock分布式锁配置
├── controller/             # 控制器层
│   ├── AuthController.java     # 认证控制器
│   ├── FlightInfoController.java # 航班控制器
│   ├── OrderController.java    # 订单控制器
│   └── ScheduledTaskController.java # 定时任务管理控制器
├── dto/                    # 数据传输对象
│   ├── AuthResponse.java       # 认证响应DTO
│   ├── FlightInfoDTO.java      # 航班信息DTO
│   ├── OrderDTO.java           # 订单DTO
│   └── OrderCreateRequest.java # 创建订单请求DTO
├── entity/                 # 实体类
│   ├── User.java              # 用户实体
│   ├── FlightInfo.java        # 航班信息实体
│   └── Order.java             # 订单实体
├── enums/                  # 枚举类
│   ├── UserRole.java          # 用户角色枚举
│   ├── FlightStatus.java      # 航班状态枚举
│   └── OrderStatus.java       # 订单状态枚举
├── repository/             # 数据访问层
│   ├── UserRepository.java    # 用户数据访问
│   ├── FlightInfoRepository.java # 航班数据访问
│   └── OrderRepository.java   # 订单数据访问
├── service/                # 服务层
│   ├── AuthService.java       # 认证服务
│   ├── UserService.java       # 用户服务
│   ├── FlightInfoService.java # 航班服务
│   ├── OrderService.java      # 订单服务
│   └── ScheduledTaskService.java # 定时任务服务
├── util/                   # 工具类
│   └── JwtUtil.java           # JWT工具类
└── AirlineOrderBackendApplication.java # 主启动类

src/main/resources/
├── application.properties  # 应用配置
└── sql/                   # SQL脚本
    ├── init.sql              # 数据库初始化脚本
    ├── create_tables.sql     # 完整表结构创建脚本
    └── test.sql              # 测试数据脚本
```

## 开发指南

### 添加新的API端点

1. **创建DTO类** (如果需要)
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewFeatureDTO {
    private String field1;
    private String field2;
    // 其他字段...
}
```

2. **在Controller中添加端点**
```java
@RestController
@RequestMapping("/api/new-feature")
public class NewFeatureController {
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<NewFeatureDTO>>> getAll() {
        // 实现逻辑
        return ResponseEntity.ok(ApiResponse.success(data, "获取成功"));
    }
}
```

3. **添加Service层逻辑**
```java
@Service
@Transactional
public class NewFeatureService {
    
    public List<NewFeatureDTO> findAll() {
        // 业务逻辑实现
    }
}
```

### 数据库迁移

当需要修改数据库结构时：

1. 修改Entity类
2. 在`src/main/resources/sql/`目录下创建迁移脚本
3. 更新`application.properties`中的`spring.jpa.hibernate.ddl-auto`配置

## ⏰ 定时任务管理

### ShedLock分布式锁

系统使用ShedLock确保在分布式环境中定时任务只在一个实例上执行，避免重复处理。

#### 配置说明

```java
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ShedLockConfig {
    
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName("shedlock")
                .build()
        );
    }
}
```

### 定时任务类型

#### 1. 取消超时待支付订单
- **执行频率**: 每5分钟执行一次
- **超时时间**: 30分钟
- **处理逻辑**: 将状态为`PENDING_PAYMENT`且创建时间超过30分钟的订单标记为`CANCELLED`

```java
@Scheduled(fixedRate = 300000) // 5分钟
@SchedulerLock(name = "cancelTimeoutPaymentOrders", lockAtMostFor = "4m", lockAtLeastFor = "1m")
public void cancelTimeoutPaymentOrders() {
    // 实现逻辑
}
```

#### 2. 处理超时出票订单
- **执行频率**: 每10分钟执行一次
- **超时时间**: 60分钟
- **处理逻辑**: 将状态为`TICKETING_IN_PROGRESS`且超过60分钟的订单标记为`TICKETING_FAILED`

#### 3. 取消长时间出票失败订单
- **执行频率**: 每小时执行一次
- **超时时间**: 24小时
- **处理逻辑**: 将状态为`TICKETING_FAILED`且超过24小时的订单标记为`CANCELLED`

#### 4. 每日维护任务
- **执行频率**: 每天凌晨2点执行
- **处理逻辑**: 清理过期数据、更新统计信息等维护操作

### 手动任务触发

系统提供管理员接口，支持手动触发各类定时任务：

```java
@RestController
@RequestMapping("/api/admin/scheduled-tasks")
public class ScheduledTaskController {
    
    @PostMapping("/cancel-timeout-payment-orders")
    public ApiResponse<Map<String, Object>> manualCancelTimeoutPaymentOrders() {
        scheduledTaskService.cancelTimeoutPaymentOrders();
        return ApiResponse.success("任务执行成功");
    }
}
```

### 监控和统计

#### 系统健康状态
- 定时任务启用状态
- 数据库连接状态
- ShedLock服务状态
- 系统运行状态

#### 任务执行统计
- 各状态订单数量统计
- 任务执行历史记录
- 系统配置参数展示

### 配置参数

```properties
# 订单超时配置
app.order.payment-timeout-minutes=30        # 待支付订单超时时间（分钟）
app.order.ticketing-timeout-minutes=60      # 出票中订单超时时间（分钟）
app.order.ticketing-failed-timeout-hours=24 # 出票失败订单自动取消时间（小时）

# 定时任务开关
app.scheduled.enabled=true                   # 定时任务总开关

# ShedLock配置
app.shedlock.default-lock-at-most-for=10m    # 默认最长锁定时间
app.shedlock.table-name=shedlock             # 锁表名称
```

### 安全配置

在`SecurityConfig.java`中配置API的访问权限：

```java
.antMatchers("/api/public/**").permitAll()           // 公开接口
.antMatchers("/api/admin/**").hasRole("ADMIN")       // 管理员接口
.antMatchers("/api/**").authenticated()              // 需要认证的接口
```

## 测试

### 单元测试
```bash
mvn test
```

### 集成测试
```bash
mvn integration-test
```

### API测试示例

使用curl测试API：

```bash
# 用户注册
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","role":"ADMIN"}'

# 用户登录
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取航班列表
curl -X GET "http://localhost:8080/api/flights"

# 创建订单 (需要JWT令牌)
curl -X POST "http://localhost:8080/api/orders" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt-token>" \
  -d '{"userId":1,"flightInfoId":1,"passengerCount":1,"passengerNames":["张三"],"contactPhone":"13800138000"}'
```

## 部署

### Docker部署

1. **创建Dockerfile**
```dockerfile
FROM openjdk:8-jre-slim
COPY target/airline-order-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

2. **构建镜像**
```bash
mvn clean package
docker build -t airline-order-backend .
```

3. **运行容器**
```bash
docker run -p 8080:8080 airline-order-backend
```

### 生产环境配置

创建`application-prod.properties`：

```properties
# 生产环境数据库配置
spring.datasource.url=jdbc:mysql://prod-db-host:3306/airline_order_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# 生产环境JWT配置
jwt.secret=${JWT_SECRET}
jwt.expiration.ms=3600000

# 日志配置
logging.level.com.postion.airlineorderbackend=INFO
logging.file.name=logs/airline-order-backend.log
```

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库服务是否启动
   - 验证连接字符串、用户名和密码
   - 确认防火墙设置

2. **JWT令牌验证失败**
   - 检查令牌是否过期
   - 验证JWT密钥配置
   - 确认请求头格式正确

3. **权限不足错误**
   - 检查用户角色是否正确
   - 验证API端点的权限配置
   - 确认JWT令牌包含正确的角色信息

### 日志查看

```bash
# 查看应用日志
tail -f logs/airline-order-backend.log

# 查看Spring Boot启动日志
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.org.springframework=DEBUG"
```

## 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/new-feature`)
3. 提交更改 (`git commit -am 'Add new feature'`)
4. 推送到分支 (`git push origin feature/new-feature`)
5. 创建Pull Request

## 许可证

本项目采用MIT许可证 - 查看[LICENSE](LICENSE)文件了解详情。

## 联系方式

- 项目维护者: qiaozhe
- 邮箱: [qiaozhe@cn.ibm.com]
- 项目地址: [项目仓库地址]

## 更新日志

### v1.0.0 (2025-07-28)
- ✅ 完成用户认证系统
- ✅ 完成航班管理功能
- ✅ 完成订单管理功能
- ✅ 集成JWT认证
- ✅ 添加Swagger文档
- ✅ 完善错误处理机制
- ✅ 实现状态机管理

### v1.1.0 (2025-08-05)
- ✅ 集成ShedLock分布式定时任务
- ✅ 实现订单超时自动处理
- ✅ 添加定时任务管理API
- ✅ 实现系统状态监控
- ✅ 支持手动任务触发
- ✅ 完善任务执行统计
- ✅ 优化订单状态流转

---

**注意**: 本项目仅用于学习和演示目的，生产环境使用前请进行充分的安全评估和性能测试。
