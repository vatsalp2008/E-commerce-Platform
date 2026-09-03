# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY shared/shared-events shared/shared-events
RUN mvn -B -f shared/shared-events/pom.xml install -DskipTests
COPY services/payment-service services/payment-service
RUN mvn -B -f services/payment-service/pom.xml package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/services/payment-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
