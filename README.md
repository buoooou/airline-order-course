# 🛫 航空订单管理系统

一个基于 Angular + Spring Boot 的现代化航空订单管理系统，支持完整的CI/CD流程和AWS云端部署。

## 📋 项目概述

### 🎯 功能特性
- ✈️ **航班管理**: 航班信息查询、管理和预订
- 👤 **用户管理**: 用户注册、登录、个人信息管理
- 📋 **订单管理**: 订单创建、支付、取消和退款
- ⏰ **定时任务**: 自动处理超时订单和系统维护
- 🔐 **安全认证**: JWT令牌认证和权限控制
- 📊 **系统监控**: 健康检查、指标监控和日志管理

### 🏗️ 技术架构
```
┌─────────────────────────────────────────────────────────────┐
│                    前端层 (Angular 20)                      │
├─────────────────────────────────────────────────────────────┤
│                    API网关层 (Nginx)                        │
├─────────────────────────────────────────────────────────────┤
│                    业务逻辑层 (Spring Boot)                  │
├─────────────────────────────────────────────────────────────┤
│                    数据持久层 (MySQL + Redis)               │
└─────────────────────────────────────────────────────────────┘
```

### 🛠️ 技术栈

**前端技术**
- Angular 20 + TypeScript
- Angular Material UI
- RxJS 响应式编程
- Angular CLI 构建工具

**后端技术**
- Spring Boot 2.7.18
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0 数据库
- Redis 缓存
- ShedLock 分布式锁

**DevOps技术**
- Docker 容器化
- GitHub Actions CI/CD
- AWS 云平台部署
- Nginx 反向代理
- Prometheus + Grafana 监控

## 🚀 快速开始

### 📋 环境要求
- Node.js 20+
- Java 8+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 8.0
- Git

### 🔧 本地开发环境搭建

1. **克隆项目**
   ```bash
   git clone https://github.com/your-username/airline-order-course.git
   cd airline-order-course
   ```

2. **启动数据库**
   ```bash
   # 使用Docker启动MySQL
   docker run -d \
     --name airline-mysql \
     -e MYSQL_ROOT_PASSWORD=rootroot \
     -e MYSQL_DATABASE=airline_order_db \
     -e MYSQL_USER=airline_user \
     -e MYSQL_PASSWORD=AirlinePass2024! \
     -p 3306:3306 \
     mysql:8.0
   ```

3. **启动后端服务**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run --spring.profiles.active=local
   ```

4. **启动前端服务**
   ```bash
   cd frontend
   npm install
   ng serve --proxy-config proxy.conf.json
   ```

5. **访问应用**
   - 前端页面: http://localhost:4200
   - 后端API: http://localhost:8080
   - API文档: http://localhost:8080/swagger-ui.html

## 🐳 Docker 部署

### 本地Docker部署
```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f app
```

### 生产环境部署
```bash
# 使用生产配置
docker-compose -f docker-compose.yml up -d

# 健康检查
curl http://localhost:8080/actuator/health
```

## ☁️ AWS 云端部署

### 🏗️ 基础设施准备

1. **使用CloudFormation自动部署**
   ```bash
   # 上传 aws-infrastructure/cloudformation-template.yaml 到AWS控制台
   # 填写必要参数后创建堆栈
   ```

2. **手动配置EC2实例**
   ```bash
   # 在EC2实例上运行初始化脚本
   wget https://raw.githubusercontent.com/your-repo/airline-order-course/main/aws-infrastructure/ec2-setup.sh
   chmod +x ec2-setup.sh
   ./ec2-setup.sh
   ```

### 🔄 CI/CD 配置

1. **配置GitHub Secrets**
   ```
   DOCKERHUB_USERNAME: 你的Docker Hub用户名
   DOCKERHUB_TOKEN: Docker Hub访问令牌
   EC2_HOST: EC2实例公网IP
   EC2_USERNAME: ubuntu
   SSH_PRIVATE_KEY: SSH私钥内容
   ```

2. **触发自动部署**
   ```bash
   git push origin main
   # GitHub Actions 将自动执行 CI/CD 流程
   ```

详细部署指南请参考: [deployment-guide.md](./deployment-guide.md)

## 📁 项目结构

```
airline-order-course/
├── frontend/                    # Angular前端项目
│   ├── src/
│   │   ├── app/                # 应用组件
│   │   ├── environments/       # 环境配置
│   │   └── assets/            # 静态资源
│   ├── angular.json           # Angular配置
│   └── package.json           # 依赖管理
├── backend/                     # Spring Boot后端项目
│   ├── src/main/java/         # Java源码
│   ├── src/main/resources/    # 配置文件
│   └── pom.xml               # Maven配置
├── aws-infrastructure/          # AWS基础设施
│   ├── cloudformation-template.yaml  # CloudFormation模板
│   └── ec2-setup.sh          # EC2初始化脚本
├── nginx/                      # Nginx配置
│   ├── nginx.conf            # 主配置文件
│   └── proxy_params          # 代理参数
├── .github/workflows/          # GitHub Actions
│   └── main.yml              # CI/CD流水线
├── docker-compose.yml          # Docker编排
├── Dockerfile                  # Docker镜像构建
├── proxy.conf.json            # 开发代理配置
└── deployment-guide.md        # 部署指南
```

## 🔧 开发指南

### 🎨 前端开发

1. **组件开发**
   ```bash
   # 生成新组件
   ng generate component components/flight-search
   
   # 生成服务
   ng generate service services/flight
   
   # 生成模块
   ng generate module modules/booking
   ```

2. **样式规范**
   - 使用 Angular Material 组件库
   - 遵循 BEM CSS 命名规范
   - 响应式设计适配移动端

3. **状态管理**
   - 使用 RxJS 进行响应式编程
   - 服务层管理应用状态
   - 组件间通信使用 EventEmitter

### 🔨 后端开发

1. **API开发**
   ```java
   @RestController
   @RequestMapping("/api/flights")
   public class FlightController {
       
       @GetMapping
       public ResponseEntity<List<Flight>> getFlights() {
           // 实现逻辑
       }
   }
   ```

2. **数据库操作**
   ```java
   @Repository
   public interface FlightRepository extends JpaRepository<Flight, Long> {
       List<Flight> findByDepartureCity(String city);
   }
   ```

3. **安全配置**
   ```java
   @Configuration
   @EnableWebSecurity
   public class SecurityConfig {
       // JWT配置
   }
   ```

### 🧪 测试

1. **前端测试**
   ```bash
   # 单元测试
   ng test
   
   # E2E测试
   ng e2e
   
   # 代码覆盖率
   ng test --code-coverage
   ```

2. **后端测试**
   ```bash
   # 单元测试
   mvn test
   
   # 集成测试
   mvn verify
   
   # 代码覆盖率
   mvn jacoco:report
   ```

## 📊 监控和维护

### 🔍 健康检查
```bash
# 应用健康状态
curl http://localhost:8080/actuator/health

# 系统信息
curl http://localhost:8080/actuator/info

# 性能指标
curl http://localhost:8080/actuator/metrics
```

### 📈 监控面板
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin123)

### 📝 日志管理
```bash
# 查看应用日志
docker logs airline-order-app

# 查看数据库日志
docker logs airline-mysql-db

# 查看Nginx日志
docker logs airline-nginx
```

## 🔒 安全配置

### 🛡️ 安全特性
- JWT令牌认证
- CORS跨域配置
- SQL注入防护
- XSS攻击防护
- CSRF保护

### 🔐 环境变量
```bash
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=airline_order_db
DB_USERNAME=airline_user
DB_PASSWORD=your-password

# JWT配置
JWT_SECRET=your-jwt-secret
JWT_EXPIRATION=86400000

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password
```

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

### 📝 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目维护者: [Your Name](mailto:your.email@example.com)
- 项目地址: https://github.com/your-username/airline-order-course
- 问题反馈: https://github.com/your-username/airline-order-course/issues

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者！

---

⭐ 如果这个项目对你有帮助，请给它一个星标！
