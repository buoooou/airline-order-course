# 航空订单后台管理系统

基于Spring Boot的航空订单后台管理系统，提供用户认证、订单管理、状态机和航班API等功能。

## 功能特性

### 1. 用户认证（JWT）
- 用户注册
- 用户登录
- JWT令牌认证
- 角色权限控制

### 2. 订单管理
- 创建订单
- 查询订单
- 支付订单
- 取消订单
- 重试出票

### 3. 状态机
订单状态流转：
- `PENDING_PAYMENT` → `PAID` → `TICKETING_IN_PROGRESS` → `TICKETED`
- `PENDING_PAYMENT` → `CANCELLED`
- `PAID` → `CANCELLED`
- `TICKETING_IN_PROGRESS` → `TICKETING_FAILED` → `TICKETING_IN_PROGRESS`

### 4. 航班API（模拟）
- 搜索航班
- 获取航班详情
- 预订航班
- 检查座位可用性

### 5. 定时任务
- 自动取消过期订单（30分钟未支付）
- 每日清理任务

## 技术栈

- **Spring Boot 2.7.18**
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据访问
- **MySQL** - 数据库
- **JWT** - 令牌认证
- **Swagger/OpenAPI** - API文档
- **Lombok** - 代码简化

## 数据库设计

### 用户表 (app_users)
```sql
CREATE TABLE `app_users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`)
);
```

### 订单表 (orders)
```sql
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_number` VARCHAR(255) NOT NULL,
  `status` ENUM('PENDING_PAYMENT', 'PAID', 'TICKETING_IN_PROGRESS', 'TICKETING_FAILED', 'TICKETED', 'CANCELLED') NOT NULL,
  `amount` DECIMAL(19, 2) NOT NULL,
  `creation_date` DATETIME(6) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `flight_number` VARCHAR(50),
  `departure_city` VARCHAR(100),
  `arrival_city` VARCHAR(100),
  `departure_time` DATETIME,
  `arrival_time` DATETIME,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_orders_user_id` FOREIGN KEY (`user_id`) REFERENCES `app_users` (`id`)
);
```

## API接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

### 订单接口
- `POST /api/orders` - 创建订单
- `GET /api/orders` - 获取用户订单
- `GET /api/orders/{orderId}` - 获取订单详情
- `POST /api/orders/{orderId}/pay` - 支付订单
- `POST /api/orders/{orderId}/cancel` - 取消订单
- `POST /api/orders/{orderId}/retry-ticketing` - 重试出票

### 航班接口
- `GET /api/flights/search` - 搜索航班
- `GET /api/flights/{flightNumber}` - 获取航班详情
- `POST /api/flights/{flightNumber}/book` - 预订航班
- `GET /api/flights/{flightNumber}/availability` - 检查座位可用性

## 运行说明

### 1. 环境要求
- Java 8+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置
1. 创建数据库：`airline_order_db`
2. 执行初始化脚本：`src/main/resources/sql/init.sql`

### 3. 应用配置
修改 `application.properties` 中的数据库连接信息：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/airline_order_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. 启动应用
```bash
mvn spring-boot:run
```

### 5. 访问地址
- 应用地址：http://localhost:8080
- Swagger文档：http://localhost:8080/swagger-ui.html

## 测试账号

初始化脚本中已创建测试账号：
- 管理员：`admin` / `password`
- 普通用户：`user` / `password`

## 项目结构

```
src/main/java/com/postion/airlineorderbackend/
├── config/                 # 配置类
│   └── SecurityConfig.java
├── controller/             # 控制器
│   ├── AuthController.java
│   ├── OrderController.java
│   └── FlightController.java
├── dto/                   # 数据传输对象
│   ├── ApiResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── OrderRequest.java
├── entity/                # 实体类
│   ├── User.java
│   └── Order.java
├── exception/             # 异常处理
│   └── GlobalExceptionHandler.java
├── repository/            # 数据访问层
│   ├── UserRepository.java
│   └── OrderRepository.java
├── security/              # 安全相关
│   ├── JwtTokenUtil.java
│   └── JwtRequestFilter.java
├── service/               # 业务逻辑层
│   ├── CustomUserDetailsService.java
│   ├── UserService.java
│   ├── OrderService.java
│   ├── FlightService.java
│   └── ScheduledTaskService.java
└── AirlineOrderBackendApplication.java
```

## 状态机说明

订单状态流转规则：
1. **待支付** → **已支付**：用户完成支付
2. **已支付** → **出票中**：系统自动开始出票
3. **出票中** → **已出票**：出票成功（90%成功率）
4. **出票中** → **出票失败**：出票失败（10%失败率）
5. **出票失败** → **出票中**：重试出票
6. **待支付** → **已取消**：超时或手动取消
7. **已支付** → **已取消**：手动取消
8. **出票失败** → **已取消**：手动取消

## 定时任务

- **过期订单取消**：每5分钟检查一次，取消30分钟未支付的订单
- **每日清理**：每天凌晨2点执行系统清理任务

## 异步处理

- **出票处理**：支付成功后异步处理出票，模拟5秒处理时间
- **成功率**：90%出票成功，10%出票失败 