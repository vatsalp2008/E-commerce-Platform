# Technical Design Document

This document provides a comprehensive overview of the **Event-Driven Microservices Architecture** of the E-commerce Platform.

---

## 1. Architecture Overview

High scalability, fault tolerance, and independent deployability are the core pillars of this architecture.

### Core Services
1. **API Gateway (8080)**: Entry point for all requests. Handles routing and CORS.
2. **Product Service (8081)**: Manages catalog, categories, and inventory-linked product data.
3. **User Service (8082)**: Handles authentication (JWT) and user profiles.
4. **Cart Service (8083)**: Manages temporary shopping sessions (Redis + Postgres).
5. **Order Service (8084)**: Orchestrates order creation and status history.
6. **Payment Service (8085)**: Mock payment processing.
7. **Inventory Service (8086)**: Real-time stock management and reservations.
8. **Notification Service (8087)**: Asynchronous communication hub.

### Technology Stack
- **Backend**: Java 17, Spring Boot 3.2, Spring Cloud Gateway
- **Database**: PostgreSQL (per service), Redis
- **Messaging**: Apache Kafka
- **Observability**: Prometheus & Grafana
- **Frontend**: React 18, TypeScript, Redux Toolkit, Tailwind CSS

### Component Diagram
```mermaid
graph TD
    User([User]) --> Gateway[API Gateway]
    Gateway --> ProductService[Product Service]
    Gateway --> UserService[User Service]
    Gateway --> CartService[Cart Service]
    Gateway --> OrderService[Order Service]
    
    OrderService -- Kafka --> PaymentService[Payment Service]
    OrderService -- Kafka --> InventoryService[Inventory Service]
    OrderService -- REST --> CartService
    
    ProductService --- DB1[(Product DB)]
    UserService --- DB2[(User DB)]
    CartService --- DB3[(Cart DB)]
    OrderService --- DB4[(Order DB)]
    PaymentService --- DB5[(Payment DB)]
    InventoryService --- DB6[(Inventory DB)]
    NotificationService --- DB7[(Notification DB)]
```

---

## 2. Event Catalog

### Order Domain
- **`orders.created`**: Produced by Order Service when a new order is initialized.
- **`orders.cancelled`**: Published when an order is voided.

### Inventory Domain
- **`inventory.reserved`**: Emitted when items are successfully held.
- **`inventory.failed`**: Emitted when stock is insufficient.

### Payment Domain
- **`payments.processed`**: Confirms successful transaction.
- **`payments.failed`**: Reports payment rejection (triggers rollback).

---

## 3. Saga Workflows (Choreography)

### Successful Order Flow
```mermaid
sequenceDiagram
    participant O as Order Service
    participant I as Inventory Service
    participant P as Payment Service
    participant N as Notification Service

    O->>O: Create Order (PENDING)
    O->>O: Save Outbox
    O-->>I: Emit OrderCreatedEvent
    I->>I: Reserve Stock
    I-->>P: Emit InventoryReservedEvent
    P->>P: Process Payment
    P-->>O: Emit PaymentProcessedEvent
    O->>O: Update Order (CONFIRMED)
    P-->>N: Emit PaymentProcessedEvent
    N->>N: Send Confirmation Email
```

### Rollback: Payment Failure
```mermaid
sequenceDiagram
    participant O as Order Service
    participant I as Inventory Service
    participant P as Payment Service
    
    I-->>P: Emit InventoryReservedEvent
    P->>P: Process Payment (FAIL)
    P-->>O: Emit PaymentFailedEvent
    P-->>I: Emit PaymentFailedEvent
    O->>O: Update Order (FAILED)
    I->>I: Release Stock (Compensation)
```
