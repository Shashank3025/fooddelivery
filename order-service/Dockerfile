FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/*.jar app.jar

# Expose service port
EXPOSE 8083

ENTRYPOINT ["java","-jar","app.jar"]