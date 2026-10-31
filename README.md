# Brightloop Subscription Platform

Unified recurring billing and subscription management platform built for scalability and high financial reliability.

## 🚀 Architecture Overview
- **Backend:** Java 21, Spring Boot 3, Maven, PostgreSQL 16, Flyway.
- **Frontend:** React, Vite, TypeScript, Vanilla CSS design system.
- **AI / LLM:** Local Ollama integration (`Qwen 2.5` / `Llama 3.1`) with rule-based fallback.

## 📂 Project Directory Structure
```
├── tasks/                       # Task breakdown & role assignments
│   ├── 00_PROJECT_OVERVIEW_AND_GATES.md
│   ├── 01_PERSON_1_FRONTEND_UI_UX.md
│   ├── 02_PERSON_2_PLATFORM_AUTH_SUPPORT.md
│   ├── 03_PERSON_3_PLANS_SUBSCRIPTIONS_BILLING.md
│   └── 04_PERSON_4_DISCOUNTS_PAYMENTS_REPORTING.md
├── TASKS.md                     # Central progress checklist
├── docker-compose.yml           # Postgres 16 & Ollama local infrastructure
├── backend/                     # Spring Boot 3 Modular Monolith
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/brightloop/subscription/
│       │   ├── BrightloopApplication.java
│       │   ├── audit/
│       │   ├── auth/
│       │   ├── billing/
│       │   ├── catalog/
│       │   ├── chatbot/
│       │   ├── common/
│       │   ├── discounts/
│       │   ├── jobs/
│       │   ├── payments/
│       │   ├── plans/
│       │   ├── reporting/
│       │   ├── subscriptions/
│       │   └── support/
│       └── main/resources/
│           ├── application.yml
│           └── db/migration/
└── frontend/                    # React + Vite + TypeScript Client
    ├── package.json
    ├── vite.config.ts
    ├── tsconfig.json
    └── src/
        ├── assets/
        ├── components/
        ├── features/
        ├── hooks/
        ├── routes/
        ├── services/
        └── types/
```

## 🛠️ Quick Start (Local Setup)

### 1. Launch Infrastructure
```bash
docker compose up -d
```

### 2. Verify Database Connection
PostgreSQL will run on `localhost:5432` (database: `brightloop_db`, user: `brightloop`, password: `brightloop_secret`).

### 3. Ollama Setup
```bash
# Pull model into Ollama container
docker exec -it brightloop-ollama ollama pull qwen2.5:3b
```

### 4. Running Backend
```bash
cd backend
mvn spring-boot:run
```

### 5. Running Frontend
```bash
cd frontend
npm install
npm run dev
```

---

## 📧 Renewal Reminders & Email Outbox

### 1. Trigger the Reminder Job Immediately (ADMIN only)
```bash
curl -X POST http://localhost:8080/api/admin/jobs/reminders \
  -u admin@brightloop.test:password
```

### 2. Renew a Subscription
```bash
curl -X POST http://localhost:8080/api/subscriptions/1/renew \
  -u cust@brightloop.test:password
```

### 3. Activate a Subscription (Generates First Invoice + PDF Email)
```bash
curl -X POST http://localhost:8080/api/subscriptions/1/activate \
  -u admin@brightloop.test:password
```

### 4. Check Email Outbox in Database
Verify the queued, sent, and failed emails:
```sql
SELECT status, attempts, last_error FROM email_outbox ORDER BY id DESC;
```

