
FROM maven:3.9-eclipse-temurin-17-alpine AS build
COPY . .
RUN mvn clean package -Dmaven.test.skip=true


FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]