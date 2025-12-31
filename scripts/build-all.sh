#!/bin/bash

# Build all microservices
services=(
  "shared/shared-events"
  "services/product-service"
  "services/user-service"
  "services/inventory-service"
  "services/cart-service"
  "services/order-service"
  "services/payment-service"
  "services/notification-service"
  "api-gateway"
)

for service in "${services[@]}"; do
  echo "Building $service..."
  cd "$service" || exit
  mvn clean install -DskipTests
  cd - || exit
done

echo "All services built successfully!"
