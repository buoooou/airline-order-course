# 在线机票预订系统

## 项目简介

这是一个完整的在线机票预订系统，包含用户管理、航班管理、订单管理、旅客管理等核心功能模块。

## 技术栈

### 后端
- **Spring Boot 3.2.0** - 主框架
- **Spring Security** - 安全认证
- **JPA/Hibernate** - 数据持久化
- **MySQL 8.0** - 数据库
- **ShedLock** - 分布式任务锁
- **MapStruct** - 对象映射
- **JWT** - 令牌认证
- **Swagger/OpenAPI** - API文档

### 前端
- **Angular 20** - 前端框架
- **Angular Material** - UI组件库
- **Tailwind CSS** - CSS框架
- **TypeScript** - 编程语言

## 数据库设计

### 主要数据表

1. **app_users_yb** - 用户表（复用）
2. **orders_yb** - 订单表（复用）
3. **airlines_yb** - 航空公司表
4. **airports_yb** - 机场表
5. **flights_yb** - 航班表
6. **passengers_yb** - 旅客表
7. **order_items_yb** - 订单明细表
8. **payments_yb** - 支付记录表
9. **permissions_yb** - 权限表
10. **role_permissions_yb** - 角色权限关联表
11. **system_configs_yb** - 系统配置表
12. **audit_logs_yb** - 操作日志表

### 数据库连接配置

```properties
数据库URL: jdbc:mysql://18.207.201.92:3306/airline_order_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
用户名: root
密码: airlineTest1234
```

## 用户角色

### 1. 游客用户 (GUEST)
- 查看航班信息
- 搜索航班

### 2. 普通用户 (USER)
- 查看航班信息
- 搜索和预订航班
- 管理个人订单
- 管理旅客信息

### 3. 管理员 (ADMIN)
- 所有用户权限
- 用户管理
- 航班管理
- 订单管理
- 系统配置
- 数据统计

## 核心功能模块

### 1. 用户管理模块

#### 功能特性
- 用户注册/登录
- JWT令牌认证
- 用户信息管理
- 角色权限控制

#### 主要API
```
POST /api/auth/register - 用户注册
POST /api/auth/login - 用户登录
GET /api/users/{id} - 获取用户信息
PUT /api/users/{id} - 更新用户信息
```

### 2. 航班管理模块

#### 功能特性
- 航班信息维护
- 航班搜索
- 座位管理
- 状态更新

#### 主要API
```
GET /api/flights - 获取航班列表
POST /api/flights/search - 搜索航班
GET /api/flights/{id} - 获取航班详情
POST /api/flights - 创建航班（管理员）
PUT /api/flights/{id} - 更新航班（管理员）
```

### 3. 订单管理模块

#### 功能特性
- 订单创建
- 订单支付
- 订单状态管理
- 退票处理

#### 主要API
```
POST /api/orders - 创建订单
GET /api/orders/{id} - 获取订单详情
PUT /api/orders/{id}/pay - 支付订单
PUT /api/orders/{id}/cancel - 取消订单
```

### 4. 旅客管理模块

#### 功能特性
- 旅客信息录入
- 证件信息管理
- 常用旅客管理

## 项目结构

```
flight-service/
├── backend/                    # Spring Boot后端
│   ├── src/main/java/com/airline/
│   │   ├── FlightServiceApplication.java
│   │   ├── config/             # 配置类
│   │   ├── controller/         # 控制器
│   │   ├── dto/                # 数据传输对象
│   │   ├── entity/             # JPA实体
│   │   ├── exception/          # 异常处理
│   │   ├── mapper/             # MapStruct映射器
│   │   ├── repository/         # 数据访问层
│   │   ├── security/           # 安全配置
│   │   └── service/            # 业务逻辑层
│   └── src/main/resources/
│       └── application.yml     # 应用配置
├── frontend/                   # Angular前端（待完成）
├── database-schema.sql         # 数据库初始化脚本
└── README.md                   # 项目说明
```

## 快速启动

### 1. 环境准备
- Java 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库初始化
```bash
# 连接到MySQL并执行初始化脚本
mysql -u root -p
source database-schema.sql
```

### 3. 启动后端服务
```bash
# 方法1: 使用启动脚本
./start-backend.sh

# 方法2: 手动启动
cd backend
mvn spring-boot:run
```

### 4. 启动前端应用
```bash
# 方法1: 使用启动脚本
./start-frontend.sh

# 方法2: 手动启动
cd frontend
npm install
npm start
```

### 5. 访问系统
- **前端应用**: http://localhost:4200
- **后端API**: http://localhost:8080/api
- **API文档**: http://localhost:8080/swagger-ui.html
- **默认管理员**: admin@airline.com / admin123

## 安全特性

1. **JWT认证** - 无状态令牌认证
2. **角色权限控制** - 基于角色的访问控制
3. **密码加密** - BCrypt加密存储
4. **CORS配置** - 跨域请求支持
5. **请求验证** - 输入数据验证

## 系统配置

### 应用配置项

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}           # JWT密钥
    expiration: 86400000           # 访问令牌过期时间（24小时）
    refresh-expiration: 604800000  # 刷新令牌过期时间（7天）
  
  cors:
    allowed-origins: http://localhost:4200,http://localhost:3000
    allowed-methods: GET,POST,PUT,DELETE,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
```

## 默认数据

### 管理员账户
- 用户名: `admin`
- 邮箱: `admin@airline.com`
- 密码: `admin123`（已加密存储）

### 示例航空公司
- 中国国际航空 (CA)
- 中国南方航空 (CZ)
- 中国东方航空 (MU)
- 海南航空 (HU)

### 示例机场
- 北京首都国际机场 (PEK)
- 上海虹桥国际机场 (SHA)
- 上海浦东国际机场 (PVG)
- 广州白云国际机场 (CAN)
- 深圳宝安国际机场 (SZX)

## 开发说明

### 1. 新增功能模块

1. 创建Entity实体类
2. 创建Repository接口
3. 创建DTO类
4. 创建MapStruct映射器
5. 创建Service接口和实现
6. 创建Controller控制器
7. 添加权限配置

### 2. 数据库迁移

新增表必须以 `_yb` 结尾，遵循项目命名规范。

### 3. API设计

- 遵循RESTful设计原则
- 统一使用 `ApiResponse<T>` 包装响应
- 使用Swagger注解生成API文档

## 功能完成状态

1. ✅ 数据库设计和初始化
2. ✅ 用户管理模块
3. ✅ 权限认证模块  
4. ✅ 航班管理模块
5. ✅ 订单管理模块
6. ✅ 旅客管理模块
7. ✅ Angular前端界面基础框架
8. ✅ API接口集成
9. ⏳ 支付集成（待扩展）
10. ⏳ 邮件通知（待扩展）
11. ⏳ 报表统计（待扩展）

## 系统测试

### API测试
```bash
# 测试用户注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"123456"}'

# 测试用户登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin@airline.com","password":"admin123"}'

# 测试航班搜索（需要先登录获取token）
curl -X GET "http://localhost:8080/api/flights?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 联系方式

如有问题请联系开发团队或查看项目文档。