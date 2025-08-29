FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/airline-order-backend-0.0.1-SNAPSHOT.jar app.jar
RUN chmod +x app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]