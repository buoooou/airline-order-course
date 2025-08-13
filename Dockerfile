# 阶段1：前端构建（带缓存优化）
FROM node:20-alpine AS frontend-builder
WORKDIR /app
COPY airline-order-frontend/package.json airline-order-frontend/package-lock.json ./
RUN npm install --prefer-offline --no-audit
COPY airline-order-frontend/ ./
RUN npm run build

# 阶段2：后端构建（带缓存优化）
FROM maven:3.8-openjdk-17 AS backend-builder
WORKDIR /app
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B
COPY backend/src ./src
COPY --from=frontend-builder /app/dist/airline-order-frontend ./src/main/resources/static
RUN mvn package -DskipTests

# 最终镜像
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=backend-builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]