# Person 2: Platform, Auth & Support - Task Breakdown

## 🎯 Role Focus
- Spring Boot 3 foundation, Maven configuration, Docker Compose environment.
- Flyway database migration lifecycle owner.
- Identity & Access Management (JWT, RBAC, password security policies).
- Audit logging subsystem.
- Chatbot engine (rule intent matching, Ollama HTTP client, ticket escalation).

---

## 📋 Task Checklist

### Phase 1: Platform & Environment Foundation (Hours 0:00 - 0:40)
- [ ] **T2.1 Docker Compose Environment**
  - Define `docker-compose.yml` for PostgreSQL 16 (`5432`) and Ollama (`11434`).
  - Configure persistent volumes and initial health check definitions.
- [ ] **T2.2 Maven & Spring Boot 3 Configuration**
  - Configure `pom.xml` with dependencies: Web, Security, Data JPA, PostgreSQL, Flyway, Lombok, Validation, JJWT.
  - Setup `application.yml` with HikariCP pool parameters, JWT secret, Ollama REST client base URL.
- [ ] **T2.3 Flyway Migration Baseline (V1 Schema)**
  - Table definitions: `users`, `password_resets`, `customers`, `audit_logs`.
  - Table definitions: `faq_articles`, `chat_sessions`, `chat_messages`, `support_tickets`, `ticket_comments`.
  - Coordinate with Person 3 and 4 for catalog, plan, invoice, and payment tables.
  - Implement single gatekeeper policy: only Person 2 adds or edits migration scripts.

---

### Phase 2: Security & Authentication Engine (Hours 0:40 - 2:00)
- [ ] **T2.4 Identity Management**
  - `User` entity with roles: `ROLE_ADMIN`, `ROLE_STAFF`, `ROLE_CUSTOMER`.
  - Password complexity validator: > 8 chars, at least one uppercase, lowercase, digit, and special character.
  - User registration endpoint: rejects duplicate emails with standard HTTP 409 Conflict.
- [ ] **T2.5 JWT & Method Security**
  - Stateless JWT token generator with access token (15m) and refresh token (7d) strategy.
  - Spring Security filter chain: `/api/auth/**` permitted, all other endpoints require authenticated JWT.
  - Method security (`@EnableMethodSecurity`, `@PreAuthorize("hasRole('ADMIN')")`).
  - Admin-only endpoint to create Staff accounts.
- [ ] **T2.6 Password Reset & Email Outbox**
  - `email_outbox` table pattern: writes password reset tokens to outbox within transactional boundary.
  - Scheduled worker or asynchronous dispatch runner.

---

### Phase 3: Audit Logging Subsystem (Hours 2:00 - 3:00)
- [ ] **T2.7 Audit Trail System**
  - `AuditLog` entity: `entity_name`, `entity_id`, `action` (CREATE, UPDATE, DELETE), `actor_id`, `changes_json`, `timestamp`.
  - Global `AuditService` or JPA entity listeners to track critical mutations (plans, discounts, billing overrides).

---

### Phase 4: Support & Chatbot Engine (Hours 3:00 - 5:00)
- [ ] **T2.8 FAQ Knowledge Repository**
  - `FaqArticle` model with keywords, title, category, content markdown.
  - Seed baseline FAQs for subscription pause, billing dates, payment receipts.
- [ ] **T2.9 Rule-Based Intent Matching**
  - Chat session endpoint `POST /api/chat/sessions` and `POST /api/chat/messages`.
  - Intent parser: extracts intents like `INVOICE_STATUS`, `SUBSCRIPTION_PAUSE`, `SPEAK_TO_HUMAN`.
  - Context isolation: strictly fetch customer's own active subscriptions and invoices; zero cross-tenant leakage.
- [ ] **T2.10 Ollama REST Client Integration**
  - Implement HTTP client via Spring's `RestClient` targeting `http://localhost:11434/api/chat`.
  - Enforce strict 5-second connection/read timeout.
  - Graceful fallback: If Ollama fails, returns rule-based templated answer immediately.
- [ ] **T2.11 Ticket Escalation Pipeline**
  - When intent is `SPEAK_TO_HUMAN` or user explicitly requests support:
  - Create `SupportTicket` with state `OPEN`, priority `MEDIUM`.
  - Attach full chronological chat transcript from `chat_messages`.
  - Staff endpoints: `GET /api/support/tickets`, `PUT /api/support/tickets/{id}/status`, `POST /api/support/tickets/{id}/comments`.
