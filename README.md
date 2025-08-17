# Airline Order System - 航班订单管理系统

# 开发环境
  win11:
        vscode, DBeaver, ubuntu, Java 17, NodeJS(10.9.3)
  ubuntu:
        Java(OpenJDK 17)，awsCLI(2.28.9), Docker(27.5.1), MySql(8.3.0)

  点击VSCode左下角的 ><, 链接 WSL, 把C盘挂载在 /mnt 下，在VSCode中打开 /mnt/cairline-order-course 就可以在ubuntu下进行开发。
  
# 后端
  Spring boot 3.5.4, 请参照 /backend/README.md 文档

# 前端
  Angular，请参照 /frontend/README.md 文档

# 数据库设计
  请参照 /backend/README.md 文档。详细请参照 \backend\src\main\resources\sql 。

# Open API
  http://localhost:8080/swagger-ui/index.html

# CI/CD
  请参照 README_CICD.md 文档。

- **用户管理**
  - 支持用户注册与登录
  - 基于角色的权限控制（ADMIN/CUSTOMER）
  - 密码BCrypt加密存储

- **订单处理**
  - 航班预订与订单创建
  - 订单状态管理（PENDING_PAYMENT/PAID/TICKETING_IN_PROGRESS/TICKETING_FAILED/TICKETED/CANCELLED）
  - 订单定时任务：取消超时未支付

- **系统能力**
  - JWT认证授权
  - 分布式锁（ShedLock）保障定时任务唯一性
  - 异步任务处理（订单通知、日志记录）
  - 完整的API文档（OpenAPI/Swagger）
  - 输入参数校验与全局异常处理

## 技术栈

- **核心框架**：Spring Boot 3.5.4
- **编程语言**：Java 17
- **安全框架**：Spring Security
- **数据访问**：Spring Data JPA
- **数据库**：MySQL
- **认证机制**：JWT (jjwt 0.11.5)
- **API文档**：SpringDoc OpenAPI 2.8.9
- **分布式锁**：ShedLock 5.14.0
- **构建工具**：Maven 3.9.11
- **开发工具**：Lombok

## 快速开始

### 本地开发环境搭建
1. fork 项目到自己的GitHub仓库
  https://github.com/buoooou/airline-order-course

2. 克隆项目到本地
git clone https://github.com/fm-t7/airline-order-course.git

3. 安装MySQL数据库，并创建名为`airline_order_db`的数据库

4. 修改`application.properties`文件，配置MySQL数据库连接信息

5. 启动项目
mvn spring-boot:run

6. 访问API文档：http://localhost:8080/swagger-ui/index.html

7. 注册新用户，登录获取JWT令牌

8. 调用API接口，测试功能


## 数据库设计

核心数据表结构：

1. **用户表(users)**：存储用户信息，包括用户名、密码（加密）、邮箱、角色等
2. **航班信息表(flight_info)**：存储航班详情，包括航班号、起降机场、时间、价格等
3. **订单表(orders)**：存储订单信息，关联用户和航班，包含订单状态、时间等
4. **分布式锁表(shedlock)**：用于定时任务的分布式锁控制

## 接口使用说明

### 认证方式

1. 通过`/api/auth/login`接口获取JWT令牌
2. 需认证接口需在请求头携带令牌：
   Authorization: Bearer <your_token>

### 主要API端点

- **认证接口**：`/api/auth/*`
  - POST `/login` - 用户登录获取令牌


- **订单接口**：`/api/orders/*`
  - GET `/` - 获取当前用户订单
  - GET `/{id}` - 获取订单详情
  - POST `/` - 创建订单
  - PUT `/{id}/status` - 更新订单状态

## 定时任务

系统包含自动标记已完成订单的定时任务：
- 执行时间：每天凌晨2点（可通过cron表达式调整）
- 功能：查询已支付且航班已到达的订单，自动标记为"COMPLETED"状态
- 采用ShedLock实现分布式锁，确保集群环境下任务唯一执行

## 响应格式

所有API接口返回统一响应格式：

```json
{
  "status": 200,
  "message": "Success",
  "data": { ... }
}
```

异常响应格式：

```json
{
  "status": 400,
  "message": "Cannot change status from CANCELLED",
  "data": null
}
```


## 项目结构

```
com.airline
├── config          # 配置类（安全、Swagger、JWT、定时任务等）
├── controller      # API接口控制器
├── dto             # 数据传输对象
├── entity          # 数据库实体类
├── exception       # 自定义异常及全局异常处理器
├── repository      # 数据访问层
├── security        # 安全相关（JWT工具、过滤器等）
├── service         # 业务逻辑层
│   ├── impl        # 服务实现
│   └── async       # 异步服务
├── task            # 定时任务
└── AirlineApplication.java  # 应用入口
```


## API文档
项目提供了完整的API文档，基于SpringDoc OpenAPI 2.8.9，可通过以下地址访问：
http://localhost:8080/swagger-ui/index.html

# 健康检查
http://localhost:8080/actuator/health