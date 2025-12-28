# Getting Started

## Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Maven

## Setup
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
   cd frontend
   npm install
   npm run dev
   ```

## API Access
- **API Gateway**: `http://localhost:8080/api`
- **Prometheus**: `http://localhost:9090`
- **Grafana**: `http://localhost:3000` (Default: admin/admin)
