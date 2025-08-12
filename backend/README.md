# 航空订票后台管理系统

## 技术栈

- 后端：Spring Boot 3.x, Hibernate, JWT, Spring Security
- 前端：Angular
- 数据库：MySQL
- 认证：JWT
- 状态管理：订单状态流转基于状态机

## 功能简介

- 管理员登录（JWT 登录）
- 订单查询（支持按状态过滤）
- 查看订单详情
- 订单状态更新（支持状态机流转）
- 调用外部航班 API 获取航班数据

## 启动方式

```bash
# 后端
./mvnw spring-boot:run

# 前端
cd frontend
npm install
ng serve  
```

## TODO
编写 DTO ↔ Entity 的转换方法（使用 MapStruct 自动生成）。

## CICD