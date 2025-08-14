FROM node:20-alpine AS frontend-build
WORKDIR /app

RUN npm install -g pnpm
COPY frontend/package.json frontend/npm-lock.yaml ./
RUN pnpm install
COPY frontend/ ./
RUN pnpm run build


FROM maven:3.8.1-openjdk-17 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline
COPY backend/src ./src

COPY --from=frontend-build /app/dist/*/browser/* ./src/main/resources/static/
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app.jar"]