#!/bin/bash

# Start all microservices in the background
# Make sure infrastructure (Postgres, Redis, Kafka) is running first!

echo "Starting Product Service (8081)..."
nohup java -jar services/product-service/target/product-service-0.0.1-SNAPSHOT.jar > logs/product.log 2>&1 &

echo "Starting User Service (8082)..."
nohup java -jar services/user-service/target/user-service-0.0.1-SNAPSHOT.jar > logs/user.log 2>&1 &

echo "Starting Cart Service (8083)..."
nohup java -jar services/cart-service/target/cart-service-0.0.1-SNAPSHOT.jar > logs/cart.log 2>&1 &

echo "Starting Order Service (8084)..."
nohup java -jar services/order-service/target/order-service-0.0.1-SNAPSHOT.jar > logs/order.log 2>&1 &

echo "Starting Payment Service (8085)..."
nohup java -jar services/payment-service/target/payment-service-0.0.1-SNAPSHOT.jar > logs/payment.log 2>&1 &

echo "Starting Inventory Service (8086)..."
nohup java -jar services/inventory-service/target/inventory-service-0.0.1-SNAPSHOT.jar > logs/inventory.log 2>&1 &

echo "Starting Notification Service (8087)..."
nohup java -jar services/notification-service/target/notification-service-0.0.1-SNAPSHOT.jar > logs/notification.log 2>&1 &

echo "Starting API Gateway (8080)..."
nohup java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar > logs/gateway.log 2>&1 &

echo "All services started! Check logs/ directory for details."
