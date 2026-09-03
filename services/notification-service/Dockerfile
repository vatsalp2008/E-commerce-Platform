# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY services/notification-service services/notification-service
RUN mvn -B -f services/notification-service/pom.xml package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/services/notification-service/target/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
