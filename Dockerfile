# =============== 阶段 1：构建 Angular 前端 ===============
FROM node:20-alpine AS frontend-builder
WORKDIR /app
# 1) 安装 pnpm（截图里的关键修复）
RUN npm install -g pnpm
# 2) 复制前端锁文件并安装依赖
COPY frontend/package.json frontend/pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
# 3) 复制其余前端源码并构建
COPY frontend/ ./
RUN pnpm build

# =============== 阶段 2：构建 Spring Boot 后端 ===============
FROM maven:3.9-eclipse-temurin-8 AS backend-builder
WORKDIR /app
# 1) 利用缓存层：先复制 pom.xml
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B
# 2) 复制源码并打包
COPY backend/src ./src
RUN mvn clean package -DskipTests

# =============== 阶段 3：生成最终运行镜像 ===============
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
# 1) 复制 jar
COPY --from=backend-builder /app/target/*.jar app.jar
# 2) 复制前端 dist 目录到 Spring Boot 的静态资源（可选）
COPY --from=frontend-builder /app/dist/frontend /app/static
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]