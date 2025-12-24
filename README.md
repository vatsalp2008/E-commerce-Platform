# AI-Powered E-commerce Platform

A full-stack e-commerce application with personalized product recommendations.

## Tech Stack
- **Frontend**: React, TypeScript, Tailwind CSS, Vite
- **Backend**: Node.js, Express, Prisma, PostgreSQL (or SQLite dev), MongoDB (Tracking)
- **ML Engine**: Python, Flask, Scikit-learn

## Setup

### 1. Environment Variables
Check `server/.env`. Default configured for local SQLite dev.

### 2. Backend
```bash
cd server
npm install
npx prisma migrate dev --name init
npx ts-node scripts/seed.ts
# Server runs on Port 8080 (to avoid MacOS conflicts)
npm run dev
```

### 3. Frontend
```bash
cd client
npm install
# Runs on localhost:5173
npm run dev
```

### 4. ML Service (Recommendation Engine)
```bash
cd ml_service
pip install -r requirements.txt
python app.py
```
*Note: The backend will work without the ML service (fallback mode).*

## Features
- **Authentication**: JWT-based auth.
- **Shopping**: Browse products, add to cart, checkout.
- **Recommendations**: Collaborative filtering based on user history.
- **Tracking**: User clicks and views logged to MongoDB (optional).
