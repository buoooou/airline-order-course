# 航空订单管理系统

基于Spring Boot 3.2.8 + Java 17的航空订单管理系统。

## 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 启动步骤

1. **启动数据库**
   ```bash
   # 使用Docker启动MySQL
   docker-compose up -d
   ```

2. **初始化数据库**
   ```sql
   -- 执行初始化脚本
   source src/main/resources/sql/init.sql
   ```

3. **启动应用**
   ```bash
   # 编译项目
   mvn clean compile
   
   # 启动应用
   mvn spring-boot:run
   ```

4. **访问应用**
   - 应用地址：http://localhost:8080
   - Swagger UI：http://localhost:8080/swagger-ui.html
   - API文档：http://localhost:8080/v3/api-docs

## 测试

运行完整测试：
```powershell
.\test-api.ps1
```

### 默认用户
- **管理员**：`admin` / `password`
- **普通用户**：`user` / `password`

## 主要功能

- ✅ 用户认证（JWT）
- ✅ 订单管理
- ✅ 定时任务
- ✅ 出票系统
- ✅ 状态机
- ✅ API文档

## 技术栈

- **框架**：Spring Boot 3.2.8
- **语言**：Java 17
- **数据库**：MySQL 8.0
- **ORM**：Spring Data JPA
- **安全**：Spring Security + JWT
- **文档**：SpringDoc OpenAPI 3
- **工具**：Lombok, MapStruct
- **定时任务**：Spring Scheduler + ShedLock

## 项目结构

```
backend/
├── src/main/java/com/position/airlineorderbackend/
│   ├── controller/          # 控制器层
│   ├── service/            # 服务层
│   ├── model/              # 实体类
│   ├── dto/                # 数据传输对象
│   ├── repo/               # 数据访问层
│   ├── security/           # 安全配置
│   ├── config/             # 配置类
│   ├── exception/          # 异常处理
│   └── scheduler/          # 定时任务
├── src/main/resources/
│   ├── application.yml     # 应用配置
│   └── sql/                # SQL脚本
├── test-api.ps1           # 测试脚本
└── README.md              # 项目说明
```

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认数据库连接配置正确

### 2. 认证失败
- 确认用户存在且密码正确
- 检查JWT配置

### 3. 定时任务不执行
- 检查ShedLock表是否存在
- 确认定时任务配置正确

## 联系方式

如有问题，请联系开发团队。
