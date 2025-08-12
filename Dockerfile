# 航空订单系统 - 多阶段构建 Dockerfile
# 这个文件将Angular前端和Spring Boot后端打包成一个镜像

# --- 阶段 1: 构建 Angular 前端 ---
FROM node:20-alpine AS frontend-builder

# 设置工作目录
WORKDIR /app

# 安装基础工具
RUN apk add --no-cache git

# 复制前端依赖文件
COPY frontend/package*.json ./
COPY frontend/angular.json ./
COPY frontend/tsconfig*.json ./

# 安装依赖（包含开发依赖，因为需要Angular CLI进行构建）
RUN npm install --legacy-peer-deps

# 复制前端源代码
COPY frontend/ ./

# 构建前端应用（禁用预渲染）
RUN npm run build -- --configuration=production --ssr=false

# --- 阶段 2: 构建 Spring Boot 后端 ---
FROM maven:3.8.5-openjdk-11 AS backend-builder

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY backend/pom.xml .

# 复制后端源代码
COPY backend/src ./src

# 创建静态资源目录
RUN mkdir -p ./src/main/resources/static

# 从前端构建阶段复制构建产物到后端静态资源目录
COPY --from=frontend-builder /app/dist/frontend/browser/* ./src/main/resources/static/

# 构建Spring Boot应用（跳过测试，使用离线模式避免网络问题）
RUN mvn clean package -DskipTests -B --fail-never || \
    mvn clean package -DskipTests -B -U

# --- 阶段 3: 创建最终运行镜像 ---
FROM openjdk:11-jre-slim

# 安装必要的运行时工具
RUN apt-get update && apt-get install -y curl tzdata && \
    ln -fs /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata && \
    rm -rf /var/lib/apt/lists/*

# 创建应用用户（安全最佳实践）
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -m appuser

# 设置工作目录
WORKDIR /app

# 从构建阶段复制JAR文件
COPY --from=backend-builder /app/target/*.jar app.jar

# 更改文件所有者
RUN chown -R appuser:appgroup /app

# 切换到非root用户
USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=prod", \
    "-Xmx512m", \
    "-Xms256m", \
    "-jar", \
    "app.jar"]
