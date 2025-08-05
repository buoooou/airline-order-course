# 后台工程

## 项目概述
这是一个为航空公司订单系统设计的后台工程，提供订单管理、用户认证、数据统计等功能。

## 技术栈
- **框架**: NestJS
- **数据库**: SQLite（开发环境）
- **API 文档**: Swagger
- **测试工具**: Jest

## 环境配置
1. 安装 Node.js（建议版本 18+）
2. 安装依赖：
   ```bash
   npm install
   ```

## 运行方式
- 启动开发服务器：
  ```bash
  npm run start:dev
  ```
- 生成 API 文档：
  ```bash
  npm run swagger
  ```
- 运行测试：
  ```bash
  npm run test
  ```

## 目录结构
```
├── src/
│   ├── modules/          # 功能模块
│   ├── shared/           # 共享模块和工具
│   ├── main.ts           # 应用入口
├── test/                 # 测试文件
├── package.json          # 项目配置
```

## 后续扩展
- 集成 Redis 缓存
- 支持 MySQL/PostgreSQL 数据库