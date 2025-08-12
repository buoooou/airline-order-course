# 构建阶段：用 Maven + JDK 21 构建 Spring Boot 应用
FROM maven:3.9.6-eclipse-temurin-21 AS backend-builder
WORKDIR /app

# 复制 Maven 配置文件，缓存依赖
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制后端代码
COPY src ./src

# 打包，跳过测试，加快构建
RUN mvn package -DskipTests

# 运行阶段：用轻量 JRE 21 镜像
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 复制构建产物
COPY --from=backend-builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令，支持外部通过环境变量覆盖 MySQL 连接配置
ENTRYPOINT ["java", "-jar", "app.jar"]
