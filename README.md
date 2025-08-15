# 航空订单系统后端

## 项目简介
航空订单系统后端是一个基于Spring Boot的RESTful API服务，提供用户认证、订单管理等功能，支持JWT身份验证和参数校验。

## 技术栈
- **框架**: Spring Boot
- **认证**: JWT (JJWT库)
- **API文档**: Swagger/OpenAPI
- **构建工具**: Maven
- **编程语言**: Java

## 环境要求
- JDK 11+ 
- Maven 3.6+ 
- MySQL 8.0+ (可选，根据项目实际需求)

## 安装与启动

### 1. 克隆项目
```bash
git clone https://github.com/yourusername/airline-order-course_liugang.git
cd airline-order-course_liugang/backend
```

### 2. 配置环境
修改`src/main/resources/application.properties`文件，配置数据库连接和JWT参数：
```properties
# 数据库配置（如果需要）
# spring.datasource.url=jdbc:mysql://localhost:3306/airline_order?useSSL=false&serverTimezone=UTC
# spring.datasource.username=root
# spring.datasource.password=password

# JWT配置
jwt.secret=63ffbc2b8d13ad5180ed7ae7c67f18c85d86046732fc9ced6a02a9d50abb1a03
jwt.expiration.ms=86400000
```

### 3. 构建项目
```bash
mvn clean package
```

### 4. 运行项目
```bash
java -jar target/airline-order-backend-0.0.1-SNAPSHOT.jar
```
或直接在IDE中运行`AirlineOrderBackendApplication`类。

## 项目结构
```
backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/postion/airlineorderbackend/
│   │   │   ├── controller/       # 控制器
│   │   │   ├── dto/              # 数据传输对象
│   │   │   ├── exception/        # 异常处理
│   │   │   ├── model/            # 模型
│   │   │   ├── service/          # 服务
│   │   │   └── AirlineOrderBackendApplication.java
│   │   └── resources/            # 资源文件
│   └── test/                     # 测试
```

## API文档
项目启动后，可访问Swagger API文档：
http://localhost:8080/swagger-ui/index.html

### 主要API

#### 认证接口
- **POST /api/auth/login** - 用户登录
  - 参数: username (字符串, 5-20字符), password (字符串, 5-20字符)
  - 返回: JWT令牌

## 异常处理
项目使用自定义`BusinessException`处理业务异常，包含HTTP状态码和错误消息。

## 许可证
本项目采用MIT许可证 - 详情见LICENSE文件。
