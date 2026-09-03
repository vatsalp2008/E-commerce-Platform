# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY shared/shared-events shared/shared-events
RUN mvn -B -f shared/shared-events/pom.xml install -DskipTests
COPY services/inventory-service services/inventory-service
RUN mvn -B -f services/inventory-service/pom.xml package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/services/inventory-service/target/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
