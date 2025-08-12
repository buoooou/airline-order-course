# 航空订单管理系统 - 项目结构说明

## 📁 项目总览

```
airline-order-course/
├── 📁 backend/                    # Spring Boot 后端服务
├── 📁 frontend/                   # Angular 前端应用
├── 📁 nginx/                      # Nginx 配置文件
├── 📁 .github/workflows/          # GitHub Actions CI/CD 配置
├── 🐳 Dockerfile                  # 后端 Docker 构建文件
├── 🐳 Dockerfile.amd64           # 后端 AMD64 架构构建文件
├── 🐳 frontend.Dockerfile        # 前端 Docker 构建文件
├── 🐳 docker-compose.aws.yml     # AWS 生产环境编排
├── 🐳 docker-compose.production.yml # 生产环境编排
├── 🚀 safe-deploy.sh             # 安全部署脚本
├── 🚀 safe-frontend-deploy.sh    # 前端部署脚本
├── 🧹 backend-cleanup.sh         # 后端清理脚本
├── 🧹 frontend-cleanup.sh        # 前端清理脚本
├── 📋 DEPLOYMENT_GUIDE.md        # 完整部署指南
├── 🔐 GITHUB_SECRETS_SETUP.md   # GitHub Secrets 配置
├── 📊 PROJECT_COST_ESTIMATION.md # 项目成本估算
├── ⏰ SCHEDULED_TASKS_README.md  # 定时任务说明
└── 🔒 SHEDLOCK_IMPLEMENTATION_SUMMARY.md # 分布式锁实现
```

## 🏗️ 后端结构 (Spring Boot)

```
backend/
├── 📁 src/main/java/com/postion/airlineorderbackend/
│   ├── 🎯 AirlineOrderBackendApplication.java    # 主启动类
│   ├── 📁 config/                                # 配置类
│   │   ├── DatabaseConfig.java                   # 数据库配置
│   │   ├── SecurityConfig.java                   # 安全配置
│   │   ├── ShedLockConfig.java                   # 分布式锁配置
│   │   └── SwaggerConfig.java                    # API 文档配置
│   ├── 📁 controller/                            # REST 控制器
│   │   ├── AuthController.java                   # 认证控制器
│   │   ├── FlightController.java                 # 航班控制器
│   │   ├── OrderController.java                  # 订单控制器
│   │   └── ScheduledTaskController.java          # 定时任务控制器
│   ├── 📁 entity/                                # 实体类
│   │   ├── User.java                             # 用户实体
│   │   ├── Flight.java                           # 航班实体
│   │   ├── Order.java                            # 订单实体
│   │   └── ScheduledTaskExecution.java          # 任务执行记录
│   ├── 📁 repository/                            # 数据访问层
│   │   ├── UserRepository.java                   # 用户仓库
│   │   ├── FlightRepository.java                 # 航班仓库
│   │   └── OrderRepository.java                  # 订单仓库
│   ├── 📁 service/                               # 业务逻辑层
│   │   ├── AuthService.java                      # 认证服务
│   │   ├── FlightService.java                    # 航班服务
│   │   ├── OrderService.java                     # 订单服务
│   │   └── ScheduledTaskService.java             # 定时任务服务
│   ├── 📁 dto/                                   # 数据传输对象
│   │   ├── LoginRequest.java                     # 登录请求
│   │   ├── ApiResponse.java                      # API 响应
│   │   └── OrderStatusUpdateRequest.java        # 订单状态更新
│   ├── 📁 security/                              # 安全相关
│   │   ├── JwtAuthenticationFilter.java          # JWT 过滤器
│   │   ├── JwtTokenProvider.java                 # JWT 令牌提供者
│   │   └── CustomUserDetailsService.java        # 用户详情服务
│   └── 📁 scheduled/                             # 定时任务
│       ├── OrderTimeoutTask.java                 # 订单超时任务
│       ├── TicketingTask.java                    # 出票任务
│       └── MaintenanceTask.java                  # 维护任务
├── 📁 src/main/resources/
│   ├── ⚙️ application.properties                 # 主配置文件
│   ├── ⚙️ application-local.properties           # 本地配置
│   ├── 📁 sql/                                   # SQL 脚本
│   │   ├── create_tables.sql                     # 建表脚本
│   │   ├── init.sql                              # 初始化数据
│   │   └── local-init.sql                        # 本地初始化
│   └── 📁 docker/                                # Docker 相关
│       └── docker-compose.yml                    # 本地开发编排
├── 📄 pom.xml                                    # Maven 配置
├── 📄 README.md                                  # 后端说明文档
└── 📄 TestReadMe.md                              # 测试说明
```

## 🎨 前端结构 (Angular)

```
frontend/
├── 📁 src/
│   ├── 🎯 main.ts                                # 应用入口
│   ├── 📄 index.html                             # HTML 模板
│   ├── 🎨 styles.scss                            # 全局样式
│   ├── 📁 app/
│   │   ├── 🎯 app.component.ts                   # 根组件
│   │   ├── 🎨 app.scss                           # 根组件样式
│   │   ├── 📄 app.html                           # 根组件模板
│   │   ├── ⚙️ app.config.ts                      # 应用配置
│   │   ├── 🛣️ app.routes.ts                      # 路由配置
│   │   ├── 📁 core/                              # 核心模块
│   │   │   ├── 📁 services/                      # 核心服务
│   │   │   │   ├── auth.ts                       # 认证服务
│   │   │   │   ├── flight.ts                     # 航班服务
│   │   │   │   └── order.ts                      # 订单服务
│   │   │   ├── 📁 models/                        # 数据模型
│   │   │   │   ├── user.model.ts                 # 用户模型
│   │   │   │   ├── flight.model.ts               # 航班模型
│   │   │   │   └── order.model.ts                # 订单模型
│   │   │   └── 📁 guards/                        # 路由守卫
│   │   │       ├── auth.guard.ts                 # 认证守卫
│   │   │       └── admin.guard.ts                # 管理员守卫
│   │   ├── 📁 pages/                             # 页面组件
│   │   │   ├── login/                            # 登录页面
│   │   │   ├── register/                         # 注册页面
│   │   │   ├── order-list/                       # 订单列表
│   │   │   ├── order-detail/                     # 订单详情
│   │   │   ├── flight-management/                # 航班管理
│   │   │   └── scheduled-tasks/                  # 定时任务管理
│   │   └── 📁 shared/                            # 共享模块
│   │       ├── 📁 components/                    # 共享组件
│   │       ├── 📁 pipes/                         # 管道
│   │       └── 📁 directives/                    # 指令
│   └── 📁 environments/                          # 环境配置
│       ├── environment.ts                        # 开发环境
│       └── environment.prod.ts                   # 生产环境
├── 📄 package.json                               # NPM 配置
├── 📄 angular.json                               # Angular 配置
├── 📄 tsconfig.json                              # TypeScript 配置
└── 📄 README.md                                  # 前端说明文档
```

## 🐳 Docker 配置

### 后端 Docker 文件
- **Dockerfile**: 基础后端构建
- **Dockerfile.amd64**: AMD64 架构优化构建

### 前端 Docker 文件
- **frontend.Dockerfile**: 多阶段构建（Node.js + Nginx）

### Docker Compose 文件
- **docker-compose.aws.yml**: AWS 生产环境
- **docker-compose.production.yml**: 通用生产环境

## 🚀 部署脚本

### 自动化部署
```bash
# 完整系统部署
./safe-deploy.sh

# 仅前端部署
./safe-frontend-deploy.sh
```

### 清理脚本
```bash
# 后端清理
./backend-cleanup.sh

# 前端清理
./frontend-cleanup.sh
```

## 🔧 配置文件

### Nginx 配置
```
nginx/
└── nginx.docker.conf                            # 生产环境 Nginx 配置
```

### GitHub Actions
```
.github/workflows/
└── ci-cd.yml                                    # CI/CD 流水线配置
```

## 📊 监控和文档

### 系统文档
- **DEPLOYMENT_GUIDE.md**: 完整部署指南
- **GITHUB_SECRETS_SETUP.md**: CI/CD 配置指南
- **PROJECT_COST_ESTIMATION.md**: 成本估算
- **SCHEDULED_TASKS_README.md**: 定时任务说明
- **SHEDLOCK_IMPLEMENTATION_SUMMARY.md**: 分布式锁实现

### 访问地址
- **前端应用**: http://18.116.240.81
- **后端API**: http://18.116.240.81:8080
- **API文档**: http://18.116.240.81:8080/swagger-ui/index.html

## 🛠️ 开发工具链

### 后端技术栈
- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **安全**: Spring Security + JWT
- **文档**: Swagger/OpenAPI
- **构建**: Maven
- **分布式锁**: ShedLock

### 前端技术栈
- **框架**: Angular 20.x
- **UI库**: Angular Material
- **语言**: TypeScript
- **样式**: SCSS
- **构建**: Angular CLI

### DevOps 工具
- **容器化**: Docker + Docker Compose
- **镜像仓库**: AWS ECR
- **CI/CD**: GitHub Actions
- **云平台**: AWS EC2
- **反向代理**: Nginx

## 🔄 开发流程

### 1. 本地开发
```bash
# 后端开发
cd backend
./mvnw spring-boot:run

# 前端开发
cd frontend
npm start
```

### 2. 构建测试
```bash
# 后端构建
cd backend
./mvnw clean package

# 前端构建
cd frontend
npm run build
```

### 3. 容器化部署
```bash
# 构建镜像
podman build -f Dockerfile.amd64 -t backend:latest .
podman build -f frontend.Dockerfile -t frontend:latest .

# 部署到生产
./safe-deploy.sh
```

### 4. CI/CD 流程
1. 代码推送到 GitHub
2. 自动触发 GitHub Actions
3. 代码质量检查和测试
4. 构建 Docker 镜像
5. 推送到 AWS ECR
6. 部署到 EC2 实例
7. 健康检查和通知

---

**项目特点**:
- ✅ 微服务架构
- ✅ 容器化部署
- ✅ 自动化 CI/CD
- ✅ 分布式锁机制
- ✅ JWT 认证授权
- ✅ 响应式前端设计
- ✅ RESTful API 设计
- ✅ 完整的监控和日志
