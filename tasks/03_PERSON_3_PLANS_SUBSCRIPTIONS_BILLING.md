# Person 3: Plans, Subscriptions & Billing - Task Breakdown

## 🎯 Role Focus
- Product catalog and variant definitions.
- Plan identity, versioning engine, and plan limits.
- Quotation templates and subscription lifecycle state machine.
- Tax rules and calculation engine.
- Scheduled and on-demand idempotent batch billing runs.

---

## 📋 Task Checklist

### Phase 1: Catalog & Plan Versioning Engine (Hours 0:40 - 2:00)
- [ ] **T3.1 Product & Variant Models**
  - Products table: `id`, `name`, `code`, `description`, `active`.
  - Product Variants table: `id`, `product_id`, `name`, `sku`, `price_delta`.
  - CRUD endpoints: Admin only (`@PreAuthorize("hasRole('ADMIN')")`).
- [ ] **T3.2 Plan Identity & Version Separation**
  - `plans` table: `id`, `code` (UNIQUE), `name`, `product_id`, `status` (`DRAFT`, `PUBLISHED`, `RETIRED`).
  - `plan_versions` table: `id`, `plan_id`, `version_number`, `price` (in paise), `billing_cycle` (`DAILY`, `WEEKLY`, `MONTHLY`, `YEARLY`), `min_quantity`, `effective_from`, `effective_to`.
  - Lifecycle flags: `auto_close`, `allow_early_close`, `allow_pause`, `allow_renewal`.
  - `plan_limits` table: `id`, `plan_version_id`, `limit_key` (e.g. `users`, `storage_gb`, `api_calls`), `limit_value`.
- [ ] **T3.3 Plan Versioning Business Logic**
  - Enforce rule: Updating a published plan NEVER mutates existing records. It increments `version_number` and creates a new row.
  - Publish verification: Plan can only be published if pricing, billing cycle, and validity window are set.
  - Retiring a plan: Prevents new subscriptions but does not alter active ones.

---

### Phase 2: Quotations & Subscription State Machine (Hours 2:00 - 3:30)
- [ ] **T3.4 Quotation Templates**
  - `quotation_templates` table: `id`, `name`, `validity_days`, `terms_text`.
  - Quotation generation: Prepares draft subscription with locked pricing for a defined period.
- [ ] **T3.5 Subscription State Machine**
  - Subscriptions table: `id`, `number` (UNIQUE sequence), `customer_id`, `plan_version_id`, `status`, `start_date`, `expiry_date`, `next_billing_date`, `is_paused`, `version` (optimistic locking).
  - Valid transitions: `DRAFT` → `QUOTATION` → `CONFIRMED` → `ACTIVE` → `CLOSED`.
  - Pause logic: Toggles `is_paused = true` only if `plan_version.allow_pause == true`. Paused subscriptions skip billing cycles.
  - Renewal logic: Extends `expiry_date` according to plan cycle without breaking continuity.
  - Early close logic: Allowed only if `plan_version.allow_early_close == true`.

---

### Phase 3: Tax Computation Engine (Hours 3:30 - 4:15)
- [ ] **T3.6 Tax Rules Management**
  - `tax_rules` table: `id`, `name`, `rate_basis_points` (e.g. 1800 for 18%), `tax_type` (GST, VAT, SALES), `is_default`, `active`.
  - Admin-only management endpoints.
  - Calculation service: Computes tax line items per invoice line using standard integer rounding once per line.

---

### Phase 4: Batch Billing Engine & Invoicing (Hours 4:15 - 5:30)
- [ ] **T3.7 Idempotent Billing Run Processor**
  - Endpoint: `POST /api/billing/runs` (Admin only, accepts target date).
  - Record execution in `job_runs`: `id`, `job_type`, `started_at`, `finished_at`, `status`, `processed_count`, `success_count`, `failure_count`.
  - Keyset pagination / chunking: Query active, unpaused subscriptions where `next_billing_date <= target_date` in batches of 500.
  - Transactional boundary per subscription:
    - Compute period start and end dates.
    - Check unique constraint on `invoices(subscription_id, period_start)`. If exists, skip.
    - Apply taxes and invoke discount engine (Person 4).
    - Generate sequential invoice number.
    - Advance subscription `next_billing_date`.
- [ ] **T3.8 Invoicing Management**
  - Invoices table: `id`, `number`, `subscription_id`, `customer_id`, `status` (`DRAFT`, `CONFIRMED`, `PAID`, `CANCELLED`), `subtotal`, `tax_total`, `discount_total`, `total_amount`, `due_date`, `version`.
  - Invoice state transitions: Confirm, cancel (only allowed from DRAFT or CONFIRMED with zero payments).
  - Print / View endpoint providing JSON representation formatted for printable receipt rendering.
