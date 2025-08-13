# 阶段1：构建 Angular前端（优化依赖安装和构建速度）
FROM node:20-alpine AS frontend-builder
WORKDIR /app

# 1. 使用国内镜像加速npm
RUN npm config set registry https://registry.npmmirror.com && \
    npm install -g npm@latest

# 2. 先复制package文件利用缓存层
COPY airline-order-frontend/package.json airline-order-frontend/package-lock.json ./
RUN npm install --prefer-offline --no-audit

# 3. 复制剩余文件（.dockerignore需排除node_modules）
COPY airline-order-frontend/ ./

# 4. 明确构建输出目录名（假设项目名为airline-order）
ARG ANGULAR_PROJECT_NAME=airline-order
RUN npm run build && \
    mv /app/dist/${ANGULAR_PROJECT_NAME} /app/dist/frontend

# 阶段2：构建 Spring Boot后端（优化Maven依赖）
FROM maven:3.8.1-openjdk-16 AS backend-builder
WORKDIR /app

# 1. 配置阿里云镜像加速
COPY settings.xml /root/.m2/
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B

# 2. 复制源代码
COPY backend/src ./src

# 3. 精确复制前端构建产物（避免通配符歧义）
COPY --from=frontend-builder /app/dist/frontend/browse/ ./src/main/resources/static/

# 4. 并行构建优化
RUN mvn package -DskipTests=true -T 1C

# 阶段3：最终运行时镜像（最小化）
FROM eclipse-temurin:17-jre-jammy  # 改用更小的JRE镜像
WORKDIR /app

# 1. 明确JAR文件名（避免通配符）
COPY --from=backend-builder /app/target/airline-order-*.jar app.jar

# 2. 安全加固：非root用户运行
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]