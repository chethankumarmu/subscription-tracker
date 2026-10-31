# Project Overview & Execution Gates

## 1. System Vision
Brightloop is a high-reliability, modular subscription management and recurring billing platform. It unifies plan lifecycle management, customer self-service, automated billing, payment settlement, finance intelligence, and AI-assisted support into a scalable modular monolith.

---

## 2. Core Architectural Principles
1. **Plan Identity vs. Plan Version:**
   - Editing an existing plan MUST never mutate historical customer pricing.
   - Any pricing or rule change creates a new `plan_version`.
   - Subscriptions strictly point to `plan_version_id`, never raw `plan_id`.
2. **Stateless Backend:**
   - Zero HTTP session state in memory. Authentication via signed JWT with role claims.
3. **Financial Integrity:**
   - Money stored as `BIGINT` paise (INR) or cents to eliminate floating-point rounding errors.
   - Taxes calculated in basis points (e.g. 1800 bp = 18.00%).
4. **Idempotency & Concurrency:**
   - Unique compound index on `invoices(subscription_id, period_start)` prevents duplicate billing.
   - Pessimistic locking (`SELECT ... FOR UPDATE`) on discount rows during invoice generation enforces usage caps under race conditions.
5. **Outbox Pattern:**
   - Background tasks (emails, notifications) written to `email_outbox` in the same transaction as domain events.

---

## 3. Strict Cut List (When Under Pressure)
If time is constrained, cut in this exact order:
1. `Plan Advisor` (AI recommendation engine)
2. `Ollama LLM Generation` (Chatbot will rely strictly on rule-based intent matching)
3. `Pricing Matrix comparison view` (Keep individual plan pricing cards)
4. `Audit Log viewer UI` (Audit table remains active in PostgreSQL)
5. `Chatbot transcript historical browser`
6. `Custom PDF print stylesheet` (Use browser default print styling)

**NEVER CUT:**
- Authentication & RBAC (Admin, Staff, Customer).
- Plan versioning isolation.
- Admin-only discount management with usage limit locks.
- Idempotent chunked billing run.
- Accurate invoice state lifecycle.
- Partial payment reconciliation.
- SQL-aggregated financial analytics.
- Chatbot-to-human ticket escalation.

---

## 4. End-to-End Demo Script (Rehearsal Checklist)
1. **Security & Identity:**
   - Register customer showing password complexity enforcement (>8 chars, uppercase, lowercase, special char).
   - Attempt duplicate registration with the same email to demonstrate unique constraint rejection.
2. **Catalog & Plan Creation:**
   - Admin creates product "Cloud Suite" with variant "Enterprise Edition".
   - Admin configures Plan "Starter" at ₹499/mo and publishes Version 1.
3. **Plan Versioning Demonstration:**
   - Admin increases price to ₹549/mo; generates Version 2.
   - Demonstrate existing active subscriptions stay pinned to Version 1.
4. **Discount Configuration:**
   - Admin sets 20% discount with a max usage limit of 5.
   - Staff user attempts to view/modify discounts; verify 403 Forbidden access.
5. **Subscription & Billing Run:**
   - Create quotation, convert to subscription, confirm and activate.
   - Trigger `POST /api/billing/runs` for current date.
   - Show generated invoice with correct tax and discount.
   - **Immediately rerun billing: show 0 new invoices generated.**
6. **Payment Processing:**
   - Record partial payment; show invoice remains `CONFIRMED` with remaining balance.
   - Record remaining balance; verify status transitions to `PAID`.
7. **Customer Portal & AI Support:**
   - Customer queries chat bot: "What is my invoice status?" (Rule-based factual return).
   - Customer requests pause.
   - Customer types: "I want to talk to an agent."
   - Verify support ticket auto-created with conversation transcript in Staff Queue.
8. **Finance Dashboard:**
   - Verify MRR, ARR, overdue aging buckets (0-30, 31-60, 60+ days) populated purely via SQL aggregations.
