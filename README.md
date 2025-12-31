# Scalable E-commerce Platform

A production-grade, event-driven e-commerce platform built with a microservices architecture using Java 17, Spring Boot 3.2, and React.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+ & Maven
- Node.js 18+
- Docker & Docker Compose

### Setup & Run
1. **Start Infrastructure**:
   ```bash
   docker-compose up -d
   ```
2. **Build All Services**:
   ```bash
   ./scripts/build-all.sh
   ```
3. **Run All Services**:
   ```bash
   ./scripts/run-all.sh
   ```
4. **Start Frontend**:
   ```bash
   cd frontend && npm install && npm run dev
   ```

### Access Points
- **Web UI**: `http://localhost:5173`
- **API Gateway**: `http://localhost:8080/api`
- **Dashboards**: Grafana (`:3000`), Prometheus (`:9090`)

---

## 🏗️ Architecture & Design

This platform demonstrates a modern distributed system using **Choreography-based Sagas** and the **Transactional Outbox** pattern for eventual consistency.

Detailed technical specifications, including event schemas and sequence diagrams, can be found in the [**Technical Design Document**](docs/DESIGN.md).

### Core Microservices
- **API Gateway**: Centralized routing and cross-cutting concerns.
- **Product & Inventory**: Catalog management and real-time stock reservation.
- **User & Cart**: JWT-based auth and high-performance session storage.
- **Order & Payment**: Saga-based checkout flow with Kafka event streaming.
- **Notification**: Multi-channel user engagement.

---
*Built for scale. Powered by Event-Driven Microservices.*
