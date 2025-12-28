# Saga Workflows (Choreography)

This document visualizes the distributed transaction flows using the Saga pattern.

## 1. Happy Path: Successful Order
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

## 2. Compensation Path: Payment Failure
```mermaid
sequenceDiagram
    participant O as Order Service
    participant I as Inventory Service
    participant P as Payment Service
    participant N as Notification Service

    O->>O: Create Order (PENDING)
    O-->>I: Emit OrderCreatedEvent
    I->>I: Reserve Stock
    I-->>P: Emit InventoryReservedEvent
    P->>P: Process Payment (FAIL)
    P-->>O: Emit PaymentFailedEvent
    P-->>I: Emit PaymentFailedEvent
    O->>O: Update Order (FAILED)
    I->>I: Release Stock (Compensation)
    P-->>N: Emit PaymentFailedEvent
    N->>N: Send Failure Notification
```

## 3. Compensation Path: Stock Insufficiency
```mermaid
sequenceDiagram
    participant O as Order Service
    participant I as Inventory Service
    participant N as Notification Service

    O->>O: Create Order (PENDING)
    O-->>I: Emit OrderCreatedEvent
    I->>I: Check Stock (FAIL)
    I-->>O: Emit InventoryReservationFailedEvent
    O->>O: Update Order (FAILED)
    O-->>N: Emit OrderStatusChangedEvent
    N->>N: Send Out of Stock Email
```
