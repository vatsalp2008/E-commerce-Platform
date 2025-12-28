# Event Catalog

This document defines the events flowing between microservices in Phase 2.

## 1. Order Service Domain

### `OrderCreatedEvent`
- **Topic**: `orders.created`
- **Producer**: Order Service
- **Consumers**: Inventory Service, Notification Service
- **Payload**:
  ```json
  {
    "orderId": "UUID",
    "userId": "UUID",
    "items": [
      { "productId": "UUID", "quantity": 1 }
    ],
    "totalAmount": 99.99,
    "timestamp": "ISO8601"
  }
  ```

### `OrderCancelledEvent`
- **Topic**: `orders.cancelled`
- **Producer**: Order Service
- **Consumers**: Inventory Service (to release stock), Notification Service
- **Payload**:
  ```json
  {
    "orderId": "UUID",
    "reason": "String",
    "timestamp": "ISO8601"
  }
  ```

## 2. Inventory Service Domain

### `InventoryReservedEvent`
- **Topic**: `inventory.reserved`
- **Producer**: Inventory Service
- **Consumers**: Payment Service, Order Service
- **Payload**:
  ```json
  {
    "orderId": "UUID",
    "allocationId": "UUID",
    "status": "SUCCESS",
    "timestamp": "ISO8601"
  }
  ```

### `InventoryReservationFailedEvent` (Compensation Start)
- **Topic**: `inventory.failed`
- **Producer**: Inventory Service
- **Consumers**: Order Service (to set status to FAILED)
- **Payload**:
  ```json
  {
    "orderId": "UUID",
    "reason": "INSUFFICIENT_STOCK",
    "timestamp": "ISO8601"
  }
  ```

## 3. Payment Service Domain

### `PaymentProcessedEvent`
- **Topic**: `payments.processed`
- **Producer**: Payment Service
- **Consumers**: Order Service, Notification Service
- **Payload**:
  ```json
  {
    "orderId": "UUID",
    "transactionId": "String",
    "status": "COMPLETED",
    "timestamp": "ISO8601"
  }
  ```

### `PaymentFailedEvent`
- **Topic**: `payments.failed`
- **Producer**: Payment Service
- **Consumers**: Order Service, Inventory Service (to release stock)
- **Payload**:
  ```json
  {
    "orderId": "UUID",
    "reason": "INSUFFICIENT_FUNDS",
    "timestamp": "ISO8601"
  }
  ```
