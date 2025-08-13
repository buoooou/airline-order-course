# 开发环境Dockerfile
FROM maven:3.8.4-openjdk-17

# 设置工作目录
WORKDIR /app

# 复制pom.xml
COPY pom.xml .

# 下载依赖
RUN mvn dependency:go-offline -B

# 暴露端口
EXPOSE 8080

# 开发模式启动
CMD ["mvn", "spring-boot:run"]
