# Brightloop Subscription Platform - Master Task Tracker

## 📌 Project Overview
- **Project Name:** Brightloop Subscription Platform
- **Architecture:** Modular Monolith (Spring Boot 3 + PostgreSQL + React Vite TS + Ollama)
- **Target Runtime:** 7.5 Hours Hackathon Execution
- **Strategy:** Parallel vertical slices per owner, gated integration, zero-code scaffold phase.

---

## 👥 Roles & Responsibilities

| Role | Owner | Focus Area | Detailed Spec |
|---|---|---|---|
| **Person 1** | Frontend & UI/UX Lead | Design System, Role Layouts, Admin Plan Editor, Portal, Chat Widget, Finance UI | [tasks/01_PERSON_1_FRONTEND_UI_UX.md](file:///d:/chethan/ODOO/tasks/01_PERSON_1_FRONTEND_UI_UX.md) |
| **Person 2** | Platform, Auth & Support | Scaffold, Flyway Migrations, Docker, JWT & RBAC, Audit Logs, Chatbot Core & Tickets | [tasks/02_PERSON_2_PLATFORM_AUTH_SUPPORT.md](file:///d:/chethan/ODOO/tasks/02_PERSON_2_PLATFORM_AUTH_SUPPORT.md) |
| **Person 3** | Plans, Subscriptions & Billing | Products/Variants, Plan Versions, Quotations, Subscription State Machine, Billing Run | [tasks/03_PERSON_3_PLANS_SUBSCRIPTIONS_BILLING.md](file:///d:/chethan/ODOO/tasks/03_PERSON_3_PLANS_SUBSCRIPTIONS_BILLING.md) |
| **Person 4** | Discounts, Payments & Reporting | Discounts & Row-Locking, Partial Payments, Overdue Aging, SQL Finance Metrics, Seed Data | [tasks/04_PERSON_4_DISCOUNTS_PAYMENTS_REPORTING.md](file:///d:/chethan/ODOO/tasks/04_PERSON_4_DISCOUNTS_PAYMENTS_REPORTING.md) |

---

## ⏱️ Milestone Gates

- [ ] **Gate 0 (0:00 - 0:40) - Foundation & Environment**
  - [ ] Docker Compose running Postgres & Ollama
  - [ ] Flyway V1 schema verified
  - [ ] Backend Spring Boot scaffold boots with health check
  - [ ] Frontend React + Vite shell runs locally
  - [ ] Shared API contract signed off

- [ ] **Gate 1 (0:40 - 3:00) - Core Subscription Pipeline**
  - [ ] User signup & password policy validation
  - [ ] Admin creates product and variant
  - [ ] Admin creates plan and publishes version 1
  - [ ] Quotation created and converted to subscription (DRAFT -> CONFIRMED)
  - [ ] Integration validation: No blockers on base pipeline

- [ ] **Gate 2 (3:00 - 5:00) - Automation, Billing & Intelligence**
  - [ ] Idempotent billing run executes for due subscriptions
  - [ ] Tax & discount calculation with row locking & redemption tracking
  - [ ] Payment recording (partial payments & invoice transition to PAID)
  - [ ] Chatbot rule-based intent matching & Ollama local generation
  - [ ] Escalation of chat session to human support ticket
  - [ ] Finance dashboard SQL metrics populated

- [ ] **Gate 3 (5:00 - 6:15) - Feature Freeze & Resilience Testing**
  - [ ] Strict feature freeze (bug fixes only)
  - [ ] Seed data injected (5,000 subscriptions, invoices, FAQs)
  - [ ] Double billing idempotency test (run billing twice: 0 new invoices)
  - [ ] Concurrent discount redemption limit test
  - [ ] Performance audit (< 2.0s query latency on seeded database)

- [ ] **Gate 4 (6:15 - 7:30) - Polish & Demo Readiness**
  - [ ] Role-based route guard and UI empty/loading states
  - [ ] Demo script rehearsed 3 times
  - [ ] Final package verification
