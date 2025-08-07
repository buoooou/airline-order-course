# 航空订单管理系统 - 前端

基于Angular 18+和Angular Material的现代化航空订单管理系统前端应用。

## 🚀 技术栈

- **框架**: Angular 18+
- **UI库**: Angular Material
- **状态管理**: RxJS
- **HTTP客户端**: Angular HttpClient
- **路由**: Angular Router
- **样式**: SCSS
- **构建工具**: Angular CLI + Vite

## ✨ 功能特性

- 🔐 **用户认证**: JWT Token认证，自动登录状态管理
- 📋 **订单管理**: 订单列表查询、详情查看、状态跟踪、自动刷新
- ✈️ **航班管理**: 航班信息管理（管理员功能）
- ⏰ **定时任务管理**: ShedLock分布式定时任务监控和管理（管理员功能）
- 📊 **实时监控**: 系统状态监控、订单统计、任务执行历史
- 🎨 **现代化UI**: Material Design设计语言
- 📱 **响应式设计**: 支持桌面端和移动端
- 🛡️ **安全防护**: HTTP拦截器、路由守卫、XSS防护
- 🌐 **国际化支持**: 中文界面，易于扩展多语言
- 🔄 **自动刷新**: 支持订单列表和系统状态的自动刷新功能

## 📁 项目结构

```
src/
├── app/
│   ├── core/                         # 核心模块
│   │   ├── models/                   # 数据模型
│   │   │   ├── user.model.ts         # 用户模型
│   │   │   └── order.model.ts        # 订单模型
│   │   ├── services/                 # 核心服务
│   │   │   ├── auth.service.ts       # 认证服务
│   │   │   ├── order.service.ts      # 订单服务
│   │   │   ├── flight.service.ts     # 航班服务
│   │   │   └── scheduled-task.service.ts # 定时任务服务
│   │   ├── guards/                   # 路由守卫
│   │   │   ├── auth.guard.ts         # 认证守卫
│   │   │   └── admin.guard.ts        # 管理员守卫
│   │   └── interceptors/             # HTTP拦截器
│   │       └── auth.interceptor.ts   # JWT拦截器
│   ├── pages/                        # 页面组件
│   │   ├── login/                    # 登录页面
│   │   │   ├── login.component.ts
│   │   │   ├── login.component.html
│   │   │   └── login.component.scss
│   │   ├── order-list/               # 订单列表页面
│   │   │   ├── order-list.ts
│   │   │   ├── order-list.html
│   │   │   └── order-list.scss
│   │   ├── order-detail/             # 订单详情页面
│   │   ├── flight-management/        # 航班管理页面
│   │   └── scheduled-tasks/          # 定时任务管理页面
│   │       └── scheduled-tasks.component.ts
│   ├── shared/                       # 共享组件
│   │   └── navbar/                   # 导航栏组件
│   ├── app.component.ts              # 根组件
│   ├── app.routes.ts                 # 路由配置
│   └── app.config.ts                 # 应用配置
├── assets/                           # 静态资源
├── environments/                     # 环境配置
└── styles/                           # 全局样式
```

## 🛠️ 开发环境设置

### 环境要求

- **Node.js**: 18.0+ 版本
- **npm**: 9.0+ 版本
- **Angular CLI**: 最新版本

### 安装步骤

1. **安装依赖**
   ```bash
   npm install
   ```

2. **启动开发服务器**
   ```bash
   ng serve
   ```

3. **访问应用**
   ```
   http://localhost:4200
   ```

### 开发命令

```bash
# 启动开发服务器
ng serve

# 启动开发服务器并自动打开浏览器
ng serve --open

# 指定端口启动
ng serve --port 4200

# 构建生产版本
ng build --configuration production

# 运行单元测试
ng test

# 运行端到端测试
ng e2e

# 代码格式检查
ng lint

# 生成新组件
ng generate component component-name

# 生成新服务
ng generate service service-name
```

## 🔧 配置说明

### 环境配置

在 `src/environments/` 目录下配置不同环境的参数：

```typescript
// environment.ts (开发环境)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  appName: '航空订单管理系统'
};

// environment.prod.ts (生产环境)
export const environment = {
  production: true,
  apiUrl: 'https://your-api-domain.com/api',
  appName: '航空订单管理系统'
};
```

### 路由配置

```typescript
// app.routes.ts
export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'orders', 
    component: OrderListComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'orders/:id', 
    component: OrderDetailComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'flights', 
    component: FlightManagementComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  { 
    path: 'scheduled-tasks', 
    component: ScheduledTasksComponent,
    canActivate: [AuthGuard, AdminGuard]
  }
];
```

### HTTP拦截器配置

```typescript
// app.config.ts
export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),
    // 其他配置...
  ]
};
```

## 🎨 UI组件库

项目使用Angular Material作为UI组件库，已配置的组件包括：

- **导航组件**: Toolbar, Sidenav, Menu
- **表单组件**: Input, Select, Checkbox, Radio, DatePicker
- **数据展示**: Table, Card, List, Chip
- **反馈组件**: Dialog, Snackbar, Progress
- **布局组件**: Grid, Divider, Expansion Panel

### 主题配置

```scss
// styles.scss
@use '@angular/material' as mat;

$primary-palette: mat.define-palette(mat.$blue-palette);
$accent-palette: mat.define-palette(mat.$pink-palette);
$warn-palette: mat.define-palette(mat.$red-palette);

$theme: mat.define-light-theme((
  color: (
    primary: $primary-palette,
    accent: $accent-palette,
    warn: $warn-palette,
  )
));

@include mat.all-component-themes($theme);
```

## ⏰ 定时任务管理

### 功能概述

定时任务管理模块是专为管理员设计的高级功能，用于监控和管理基于ShedLock的分布式定时任务系统。

### 主要功能

#### 1. 系统状态监控
- **实时状态显示**: 系统运行状态、定时任务开关状态
- **数据库连接监控**: 实时检测数据库连接状态
- **ShedLock状态**: 分布式锁服务状态监控
- **自动刷新**: 支持手动和自动刷新系统状态

#### 2. 订单统计监控
- **实时订单统计**: 各状态订单数量实时统计
- **状态分类显示**: 待支付、已支付、出票中、出票失败、已出票、已取消
- **高亮提醒**: 异常状态订单高亮显示
- **趋势监控**: 订单状态变化趋势分析

#### 3. 定时任务配置
- **超时时间配置**: 显示各类订单的超时处理时间
- **任务状态显示**: 定时任务启用/禁用状态
- **配置信息展示**: 系统配置参数实时显示

#### 4. 手动任务执行
- **取消超时待支付订单**: 手动触发超时订单取消任务
- **处理超时出票订单**: 手动处理出票超时的订单
- **取消长时间失败订单**: 清理长时间出票失败的订单
- **每日维护任务**: 执行系统日常维护操作

#### 5. 任务执行历史
- **执行记录**: 完整的任务执行历史记录
- **时间线显示**: 直观的时间线界面展示
- **状态标记**: 成功/失败状态清晰标记
- **详细信息**: 任务类型、执行时间、执行结果

#### 6. 实时订单监控
- **订单列表**: 实时显示系统中的关键订单
- **状态标签**: 彩色状态标签便于识别
- **详细信息**: 订单号、金额、创建时间、乘客信息
- **快速筛选**: 按状态快速筛选订单

### 技术实现

```typescript
// scheduled-task.service.ts
export class ScheduledTaskService {
  private readonly API_URL = `${environment.apiUrl}/admin/scheduled-tasks`;

  // 获取系统健康状态
  async getSystemHealth(): Promise<ApiResponse<SystemHealth>> {
    return this.http.get<ApiResponse<SystemHealth>>(`${this.API_URL}/health`).toPromise();
  }

  // 获取任务配置
  async getTaskConfig(): Promise<ApiResponse<TaskConfig>> {
    return this.http.get<ApiResponse<TaskConfig>>(`${this.API_URL}/config`).toPromise();
  }

  // 执行手动任务
  async executeTask(taskType: string): Promise<ApiResponse<TaskExecution>> {
    return this.http.post<ApiResponse<TaskExecution>>(`${this.API_URL}/${taskType}`, {}).toPromise();
  }

  // 获取任务统计
  async getTaskStatistics(): Promise<ApiResponse<TaskStatistics>> {
    return this.http.get<ApiResponse<TaskStatistics>>(`${this.API_URL}/statistics`).toPromise();
  }
}
```

### 使用说明

1. **访问权限**: 仅管理员用户可以访问定时任务管理页面
2. **页面路径**: `/scheduled-tasks`
3. **自动刷新**: 支持每30秒自动刷新系统状态
4. **手动操作**: 所有任务都支持手动触发执行
5. **状态监控**: 实时监控系统运行状态和订单变化

### 安全考虑

- **权限控制**: 通过AdminGuard确保只有管理员可以访问
- **操作确认**: 重要操作需要用户确认
- **日志记录**: 所有操作都有完整的日志记录
- **错误处理**: 完善的错误处理和用户提示

## 🔐 认证和权限

### JWT Token管理

```typescript
// auth.service.ts
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  
  // 保存Token
  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }
  
  // 获取Token
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }
  
  // 检查是否已登录
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
```

### 路由守卫

```typescript
// auth.guard.ts
export const AuthGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isLoggedIn()) {
    return true;
  } else {
    router.navigate(['/login']);
    return false;
  }
};
```

### HTTP拦截器

```typescript
// auth.interceptor.ts
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  
  if (token) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(authReq);
  }
  
  return next(req);
};
```

## 🔄 自动刷新功能

### 功能概述

系统提供了智能的自动刷新功能，确保用户始终看到最新的数据状态。

### 实现特性

#### 1. 订单列表自动刷新
- **开关控制**: 使用Material Design的滑动开关控制
- **刷新频率**: 每30秒自动刷新一次
- **静默刷新**: 后台刷新不影响用户操作
- **状态提示**: 显示最后更新时间和刷新状态
- **变化检测**: 检测到订单状态变化时显示通知

#### 2. 定时任务页面自动刷新
- **系统状态**: 实时监控系统运行状态
- **订单统计**: 自动更新各状态订单数量
- **手动触发**: 支持手动立即刷新
- **加载指示**: 清晰的加载状态指示

### 技术实现

```typescript
// 自动刷新实现示例
export class OrderListComponent {
  autoRefreshEnabled = false;
  autoRefreshSubscription: Subscription | null = null;

  toggleAutoRefresh(): void {
    if (this.autoRefreshEnabled) {
      // 开启自动刷新（每30秒）
      this.autoRefreshSubscription = interval(30000).subscribe(() => {
        this.loadOrders(true); // 静默刷新
      });
      this.showSuccess('已开启自动刷新（每30秒）');
    } else {
      // 停止自动刷新
      if (this.autoRefreshSubscription) {
        this.autoRefreshSubscription.unsubscribe();
        this.autoRefreshSubscription = null;
      }
      this.showSuccess('已停止自动刷新');
    }
  }

  // 静默刷新，不显示加载状态
  loadOrders(silent: boolean = false): void {
    if (!silent) {
      this.loading = true;
    } else {
      this.refreshing = true;
    }
    
    // 检查数据变化并提示用户
    if (silent && this.hasOrderChanges(previousOrders, newOrders)) {
      this.showSuccess('📊 订单数据已更新！发现状态变化');
    }
  }
}
```

### 用户体验优化

- **非侵入式**: 自动刷新不会打断用户当前操作
- **智能提示**: 只在数据发生变化时提示用户
- **状态保持**: 刷新时保持用户的操作状态
- **错误处理**: 网络错误时不影响用户体验

## 📱 响应式设计

项目采用Angular Flex Layout和CSS Grid实现响应式设计：

```scss
// 响应式断点
$mobile: 480px;
$tablet: 768px;
$desktop: 1024px;

// 移动端适配
@media (max-width: $mobile) {
  .container {
    padding: 8px;
  }
  
  .mat-card {
    margin: 8px 0;
  }
}

// 平板适配
@media (max-width: $tablet) {
  .sidebar {
    display: none;
  }
  
  .main-content {
    margin-left: 0;
  }
}
```

## 🧪 测试

### 单元测试

```bash
# 运行所有测试
ng test

# 运行测试并生成覆盖率报告
ng test --code-coverage

# 运行特定测试文件
ng test --include="**/auth.service.spec.ts"
```

### 端到端测试

```bash
# 运行E2E测试
ng e2e

# 运行E2E测试（无头模式）
ng e2e --headless
```

### 测试示例

```typescript
// auth.service.spec.ts
describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should login successfully', () => {
    const mockResponse = { token: 'fake-token', username: 'test' };
    
    service.login({ username: 'test', password: 'test' }).subscribe(response => {
      expect(response.data.token).toBe('fake-token');
    });

    const req = httpMock.expectOne(`${service.API_URL}/login`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, data: mockResponse });
  });
});
```

## 🚀 构建和部署

### 开发构建

```bash
ng build
```

### 生产构建

```bash
ng build --configuration production
```

### Docker部署

```dockerfile
# Dockerfile
FROM node:18-alpine as build

WORKDIR /app
COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Nginx配置

```nginx
# nginx.conf
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # 处理Angular路由
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API代理
    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 🔧 常见问题

### 1. 启动时出现端口占用错误

```bash
# 查看端口占用
lsof -i :4200

# 使用其他端口启动
ng serve --port 4201
```

### 2. 依赖安装失败

```bash
# 清除npm缓存
npm cache clean --force

# 删除node_modules重新安装
rm -rf node_modules package-lock.json
npm install
```

### 3. 构建时内存不足

```bash
# 增加Node.js内存限制
export NODE_OPTIONS="--max-old-space-size=8192"
ng build --configuration production
```

### 4. CORS跨域问题

在开发环境中，可以使用代理配置解决跨域问题：

```json
// proxy.conf.json
{
  "/api/*": {
    "target": "http://localhost:8080",
    "secure": true,
    "changeOrigin": true
  }
}
```

```bash
# 使用代理启动
ng serve --proxy-config proxy.conf.json
```

## 📚 学习资源

- [Angular官方文档](https://angular.io/docs)
- [Angular Material组件库](https://material.angular.io/)
- [RxJS操作符指南](https://rxjs.dev/guide/operators)
- [TypeScript手册](https://www.typescriptlang.org/docs/)

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支: `git checkout -b feature/new-feature`
3. 提交更改: `git commit -am 'Add new feature'`
4. 推送分支: `git push origin feature/new-feature`
5. 提交Pull Request

## 📄 许可证

本项目采用MIT许可证 - 查看[LICENSE](../LICENSE)文件了解详情。

---

**Happy Coding! 🚀**
