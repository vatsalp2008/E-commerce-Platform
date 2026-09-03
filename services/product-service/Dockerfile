# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY services/product-service services/product-service
RUN mvn -B -f services/product-service/pom.xml package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/services/product-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
