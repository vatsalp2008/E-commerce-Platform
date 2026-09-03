# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY services/user-service services/user-service
RUN mvn -B -f services/user-service/pom.xml package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/services/user-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
