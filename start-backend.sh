#!/bin/bash

echo "启动在线机票系统后端服务..."

cd backend

echo "检查MySQL连接..."
# You can add MySQL connection check here

echo "启动Spring Boot应用..."
mvn spring-boot:run

echo "后端服务已启动，访问地址: http://localhost:8080"
echo "API文档地址: http://localhost:8080/swagger-ui.html"