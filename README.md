# Scalable E-commerce Platform

A production-grade, event-driven e-commerce platform built with a microservices architecture using Java 17, Spring Boot 3.2, and React.

## 🚀 Overview

This project demonstrates a modern distributed system architecture designed for high scalability and availability. It transitions from a monolithic structure to a set of independent, specialized microservices.

## 🏗️ Architecture

The platform consists of several core components:

### Backend Microservices
- **API Gateway (8080)**: Central entry point and request router.
- **Product Service (8081)**: Manages product catalog and categories.
- **User Service (8082)**: Authentication and profile management (JWT-based).
- **Cart Service (8083)**: Shopping cart management using Redis and PostgreSQL.
- **Order Service (8084)**: Orchestrates the checkout and order lifecycle.
- **Payment Service (8085)**: Mock payment processing system.
- **Inventory Service (8086)**: Real-time stock management and reservations.
- **Notification Service (8087)**: Multi-channel notification hub.

### Frontend
- **E-Shop Web**: A premium React application built with TypeScript, Redux Toolkit, and Tailwind CSS.

### Infrastructure
- **Databases**: Separate PostgreSQL instances for each microservice.
- **Caching**: Redis for high-performance session and cart storage.
- **Messaging**: Apache Kafka (Phase 2) for asynchronous event-driven communication.
- **Monitoring**: Prometheus and Grafana for system health and metrics.

## 🛠️ Quick Start

Detailed instructions can be found in [docs/GETTING_STARTED.md](docs/GETTING_STARTED.md).

1. **Infrastructure**: `docker-compose up -d`
2. **Build**: `./scripts/build-all.sh`
3. **Run**: `./scripts/run-all.sh`

## 📚 Documentation

- [Architecture Overview](docs/ARCHITECTURE.md)
- [Project Progress](docs/PROGRESS.md)
- [Getting Started Guide](docs/GETTING_STARTED.md)

---
*Built for scale. Powered by Event-Driven Microservices.*
