## 航空订单管理系统

### 1.环境

- java version "21.0.7" 2025-04-15 LTS
- Apache Maven 3.8.9
- jdk-21.0.7+6
- SpringBoot 3.5.4
- mysql Ver 9.2.0
- backend IDE：vscode

### 2.项目结构

- src/
- ├── main/
- │ ├── java/
- │ │ └── com.position.airline_order_course/
- │ │ ├── controller/ #控制器层
- │ │ ├── service/ #业务层
- │ │ ├── repo/ #数据访问层
- │ │ ├── model/ #实体类
- │ │ ├── config/ #配置类
- │ │ ├── dto/ #数据传输
- │ │ ├── exception/ #系统异常类
- │ │ ├── mapper/ #数据转换
- │ │ ├── task/ #任务处理
- │ │ ├── util/ #辅助（通用工具等）
- │ │ └── AirlineOrderCourseApplication.java #启动类
- │ └── resources/
- │ ├── application.properties #配置文件
- │ └── mysql.session.sql #数据库脚本
- └── test/ # 单元测试

### 3.功能概要（homework for August 2nd）

- 3.1 JWT 鉴权 主要使用类
- JwtAuthFilter.java
- JwtProperties.java
- SecurityConfig.java
- UserPwdAuthProvider.java
- User.java
- LoginRequestDto.java
- AuthController.java
- UserDetailsServiceImpl.java
- UserRepository.java
- JwtResponse.java
- JwtUtil.java

- 3.2 ShedLock 定时任务 主要使用类
- ShedLockConfig.java
- OrderCancelTask.java
- OrderRepository.java
- Order.java

- 3.3 模拟出票业务 主要使用类
- 出票业务结果（成功/失败）都通过 TicketResponse.success/error 返回
- 异常只用于系统错误，空指针等情况
- TicketController.java
- MockTicketService.java
- MockTicketServiceImpl.java
- TicketResponse.java
- GlobalException.java

- 3.4 Swagger API 文档
- SwaggerConfig.java
