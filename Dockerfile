# --- 阶段1：构建Angular前端（Node.js 20） ---
FROM node:20-alpine AS frontend-builder
WORKDIR /app
# 复制前端依赖文件
COPY frontend/package.json frontend/package-lock.json ./
# 安装依赖（npm 10.9，Node 22默认兼容）
RUN npm install
# 复制前端源码并构建
COPY frontend/ ./
RUN npm run build  # 生成产物默认在dist/[项目名]/browser

# --- 阶段2：构建Spring Boot后端（OpenJDK 11） ---
FROM maven:3.8.5-openjdk-11 AS backend-builder
WORKDIR /app
# 缓存Maven依赖
COPY backend/pom.xml .
RUN mvn dependency:go-offline
# 复制后端源码
COPY backend/src ./src
# 复制前端构建产物到后端静态资源目录（供Spring Boot直接访问）
COPY --from=frontend-builder /app/dist/*/browser/* ./src/main/resources/static/
# 打包后端（跳过测试加速构建）
RUN mvn package -DskipTests

# --- 阶段3：最终运行镜像（轻量JRE） ---
FROM openjdk:11-jre-slim

# 安装必要的运行时工具
RUN apt-get update && apt-get install -y --no-install-recommends curl tzdata && \
    ln -fs /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata && \
    rm -rf /var/lib/apt/lists/*

# 创建应用用户（安全最佳实践）
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -m appuser

WORKDIR /app

# 复制后端JAR包
COPY --from=backend-builder /app/target/*.jar app.jar

# 更改文件所有者
RUN chown -R appuser:appgroup /app

# 切换到非root用户
USER appuser

# 容器对外暴露8080端口
EXPOSE 8080

# 启动命令（可添加JVM参数）
ENTRYPOINT ["java", "-jar", "app.jar"]