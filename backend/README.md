# 航空公司订单系统后台

## 项目概述
基于 Spring Boot 的后台工程，提供订单管理、用户认证等功能。

## 技术栈
- **框架**: Spring Boot 3.1.5
- **数据库**: PostgreSQL（配置见 `application.properties`）
- **认证**: JWT
- **API 文档**: 未集成（可添加 Swagger）
- **数据库管理**: Hibernate 自动建表（`create-drop` 模式）

## 核心功能
1. **用户认证**：通过 JWT 实现登录与权限控制。
2. **订单管理**：支持订单的创建、查询、更新和删除。
3. **数据模型**：定义订单、用户等核心实体。

## 运行方式
1. 配置数据库连接（编辑 `application.properties`）。
2. 启动项目：
   ```bash
   mvn spring-boot:run
   ```

## 目录结构
- `config/`: 安全与 JWT 配置
- `controller/`: API 接口
- `service/`: 业务逻辑
- `model/`: 数据实体
- `repo/`: 数据库访问层
- `util/`: 工具类

## 注意事项
- 需根据实际需求完善数据库配置。
- 如需 API 文档，建议集成 Swagger。