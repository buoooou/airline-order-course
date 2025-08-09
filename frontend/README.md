# 航空订单管理系统 - 前端

这是一个基于 Angular 17 的航空订单管理系统前端应用。

## 功能特性

- 🔐 **用户认证**: 登录和注册功能，使用 JWT 令牌
- 📋 **订单管理**: 查看、创建、支付、取消订单
- 🔄 **状态机**: 订单状态流转（待支付 → 已支付 → 出票中 → 已出票）
- 📱 **响应式设计**: 适配桌面和移动设备
- 🎨 **现代UI**: 美观的用户界面

## 技术栈

- **Angular 17**: 前端框架
- **TypeScript**: 开发语言
- **SCSS**: 样式预处理器
- **RxJS**: 响应式编程
- **Angular Router**: 路由管理

## 项目结构

```
src/
├── app/
│   ├── core/
│   │   ├── guards/
│   │   │   └── auth.guard.ts          # 认证路由守卫
│   │   ├── interceptors/
│   │   │   └── auth.interceptor.ts     # HTTP拦截器
│   │   └── services/
│   │       ├── auth.service.ts         # 认证服务
│   │       └── order.service.ts        # 订单服务
│   ├── pages/
│   │   ├── login/
│   │   │   └── login.component.ts      # 登录页面
│   │   ├── register/
│   │   │   └── register.component.ts   # 注册页面
│   │   ├── orders/
│   │   │   └── orders.component.ts     # 订单列表页面
│   │   ├── order-detail/
│   │   │   └── order-detail.component.ts # 订单详情页面
│   │   └── create-order/
│   │       └── create-order.component.ts # 创建订单页面
│   ├── app.component.ts                # 主应用组件
│   ├── app.config.ts                   # 应用配置
│   └── app.routes.ts                   # 路由配置
```

## 页面功能

### 1. 登录页面 (`/login`)
- 用户登录表单
- 用户名和密码验证
- 登录成功后跳转到订单列表

### 2. 注册页面 (`/register`)
- 用户注册表单
- 角色选择（普通用户/管理员）
- 注册成功后跳转到登录页面

### 3. 订单列表页面 (`/orders`)
- 显示用户的所有订单
- 订单状态标识和操作按钮
- 支持支付、取消、重试出票等操作
- 创建新订单的入口

### 4. 订单详情页面 (`/orders/:id`)
- 显示订单的详细信息
- 订单状态时间线
- 根据订单状态显示相应的操作按钮

### 5. 创建订单页面 (`/orders/create`)
- 订单创建表单
- 航班信息输入
- 金额设置

## 订单状态

- **PENDING_PAYMENT**: 待支付
- **PAID**: 已支付
- **TICKETING_IN_PROGRESS**: 出票中
- **TICKETING_FAILED**: 出票失败
- **TICKETED**: 已出票
- **CANCELLED**: 已取消

## 安装和运行

### 前置条件
- Node.js 18+ 
- npm 或 yarn

### 安装依赖
```bash
cd frontend
npm install
```

### 开发模式运行
```bash
npm start
```

应用将在 `http://localhost:4200` 启动。

### 构建生产版本
```bash
npm run build
```

## API 集成

前端应用需要连接到后端 API 服务（默认运行在 `http://localhost:8080`）。

### 主要 API 端点

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/orders` - 获取用户订单列表
- `POST /api/orders` - 创建新订单
- `GET /api/orders/:id` - 获取订单详情
- `POST /api/orders/:id/pay` - 支付订单
- `POST /api/orders/:id/cancel` - 取消订单
- `POST /api/orders/:id/retry-ticketing` - 重试出票

## 开发指南

### 添加新页面
1. 在 `src/app/pages/` 下创建新的组件
2. 在 `src/app/app.routes.ts` 中添加路由配置
3. 如果需要认证保护，添加 `canActivate: [AuthGuard]`

### 添加新服务
1. 在 `src/app/core/services/` 下创建新的服务
2. 使用 `@Injectable({ providedIn: 'root' })` 装饰器

### 样式指南
- 使用 SCSS 进行样式开发
- 采用组件级别的样式封装
- 响应式设计，支持移动端

## 部署

### 构建
```bash
npm run build
```

### 部署到静态服务器
将 `dist/frontend/browser` 目录的内容部署到 Web 服务器。

## 故障排除

### 常见问题

1. **CORS 错误**: 确保后端已配置 CORS 支持
2. **认证失败**: 检查 JWT 令牌是否正确
3. **API 连接失败**: 确认后端服务正在运行

### 调试
- 使用浏览器开发者工具查看网络请求
- 检查控制台错误信息
- 验证 API 端点是否正确

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License
