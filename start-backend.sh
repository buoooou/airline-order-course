#!/bin/bash

echo "启动在线机票系统后端服务..."

cd backend

echo "设置环境变量..."
export SPRING_DATASOURCE_URL="jdbc:mysql://51.21.243.217:3306/airline_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="FUser32AirlineDB"
export JWT_SECRET="PkpDrzdV7IN0ehzqC6fxwh7yOJccs6I/LqSWIiCZWGEYb8IcBeb/Dl7OWE52ZldC"
export JWT_EXPIRATION="86400000"
export JWT_REFRESH_EXPIRATION="604800000"
export CORS_ALLOWED_ORIGINS="http://localhost:4200,http://localhost:3000,http://localhost:4201"
export JWT_ISSUER_URI="http://localhost:8080"

echo "检查MySQL连接..."
echo "数据库连接配置: $SPRING_DATASOURCE_URL"

echo "启动Spring Boot应用..."
mvn spring-boot:run

echo "后端服务已启动，访问地址: http://localhost:8080"
echo "API文档地址: http://localhost:8080/swagger-ui.html"