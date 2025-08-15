# 前端构建阶段
FROM node:20-alpine AS frontend-build
WORKDIR /app

# 使用 pnpm 并安装依赖
RUN npm install -g pnpm
COPY frontend/package.json frontend/pnpm-lock.yaml ./
RUN pnpm install

# 复制前端源码并构建
COPY frontend/ ./
RUN pnpm run build

# 后端构建阶段
FROM maven:3.8.1-openjdk-17 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src

# 从前端构建阶段复制静态资源
COPY --from=frontend-build /app/dist/frontend/browser/ ./src/main/resources/static/

# 打包应用
RUN mvn package -DskipTests

# 最终运行时镜像
FROM eclipse-temurin:17-jre-alpine

# 安装 Nginx (从 2.txt 新增)
RUN apk add --no-cache nginx

WORKDIR /app

# 从构建阶段复制制品
COPY --from=backend-build /app/target/*.jar app.jar

# 复制前端资源到 Nginx 目录 (从 2.txt 新增)
COPY --from=frontend-build /app/dist/frontend/browser/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/http.d/default.conf

# 改进的启动脚本 (从 2.txt 优化)
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'nginx &' >> /app/start.sh && \
    echo 'exec java -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

EXPOSE 80 8080
CMD ["sh", "/app/start.sh"]