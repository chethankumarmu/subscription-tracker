# Brightloop Database: Detailed Design

This covers what each table stores, why it exists, the key rules, and how data flows between tables. Use it as the source for Flyway migration V1.

## 1. Conventions (apply to every table)

| Concern | Rule |
|---|---|
| Primary keys | `BIGINT GENERATED ALWAYS AS IDENTITY` for internal rows. Public-facing numbers (invoices, subscriptions) get a separate human-readable `number` column. |
| Timestamps | `created_at`, `updated_at` as `TIMESTAMPTZ`, stored in UTC. Display in Asia/Kolkata in the UI. |
| Money | `BIGINT` in paise. Never `FLOAT` or `DOUBLE`. Currency is INR for now, with a `currency CHAR(3)` column on invoices and payments so it can extend later. |
| Tax rates | `INTEGER` in basis points (1800 = 18%). |
| Concurrency | `version INTEGER NOT NULL DEFAULT 0` on mutable business tables (subscriptions, invoices, discounts). Hibernate `@Version` uses it. |
| Soft delete | Not used for financial records. Use status (CANCELLED, CLOSED, INACTIVE) instead. Deleting an invoice would break the audit trail. |
| Enums | Stored as `VARCHAR` with a `CHECK` constraint, not Postgres `ENUM` types. Easier to alter in Flyway migrations. |
| Naming | `snake_case`, plural table names, `_id` foreign keys. |

## 2. Table-by-table design

### 2.1 Identity and access

**`users`**: one row per login account, of any role.
- `id`, `email` (UNIQUE, stored lowercased), `password_hash`, `full_name`
- `role` CHECK IN ('ADMIN','STAFF','CUSTOMER')
- `is_active` BOOLEAN, `last_login_at`, `created_at`, `updated_at`
- Only admins can create STAFF rows. The signup endpoint can only create CUSTOMER rows.

**`password_resets`**: one row per reset request.
- `id`, `user_id`, `token_hash` (SHA-256 of the token; the raw token is only emailed), `expires_at` (30 min), `used_at`
- Old tokens are invalidated when a new one is requested.

**`customers`**: the business account a subscription belongs to. One user can own one customer profile.
- `id`, `user_id` (UNIQUE, nullable for customers added by staff who never log in), `company_name`, `contact_name`, `phone`, `billing_address` (JSONB or columns), `gstin` (optional), `status` (ACTIVE, INACTIVE)
- Separating customers from users lets staff create a customer for a company that hasn't signed up yet.

### 2.2 Catalog

**`products`**: what the company sells.
- `id`, `sku` (UNIQUE), `name`, `product_type` (SERVICE, ADDON, BUNDLE), `sales_price_paise`, `cost_price_paise`, `notes`, `is_active`
- `cost_price` is for internal margin reports only. It never appears on customer invoices.

**`product_variants`**: alternative versions of a product, with a price adder.
- `id`, `product_id`, `name` (for example "Odoo"), `attribute` (for example "brand"), `price_adder_paise` (Odoo = 56000), `is_active`
- UNIQUE(`product_id`, `name`)
- Final price for a variant = product sales price + price adder.

### 2.3 Plans (versioned)

**`plans`**: the stable identity of a plan.
- `id`, `code` (UNIQUE, for example "STARTER"), `name`, `product_id`, `status` (DRAFT, PUBLISHED, RETIRED), `current_version_id` (FK to `plan_versions`, nullable until first publish)

**`plan_versions`**: the actual terms. Each change to a published plan creates a new row here.
- `id`, `plan_id`, `version_no` (UNIQUE per plan), `price_paise`, `billing_cycle` (DAILY, WEEKLY, MONTHLY, YEARLY), `min_quantity`
- `available_from`, `available_to` (DATE)
- Lifecycle flags: `auto_close`, `allow_early_close`, `allow_pause`, `allow_renewal` (all BOOLEAN)
- `effective_from` (TIMESTAMPTZ), `effective_to` (NULL while this is the active version)
- `created_by` (user id of the admin who created it)
- Rule: a published version is immutable. Edits create a new version.

**`plan_limits`**: per-version limits, one row per limit type.
- `id`, `plan_version_id`, `limit_key` (USERS, STORAGE_GB, API_CALLS, CUSTOM_DOMAINS, and so on), `limit_value` (NULL means unlimited)
- UNIQUE(`plan_version_id`, `limit_key`)
- Using rows instead of columns means new limit types don't need a migration.

**`quotation_templates`**: reusable offer skeletons for sales reps.
- `id`, `name`, `plan_version_id`, `quantity`, `valid_days`, `is_active`
- Staff create a quotation from a template, which copies its lines into a subscription in DRAFT.

### 2.4 Tax and discounts

**`tax_rules`**: admin-defined tax rates.
- `id`, `name` (for example "GST 18%"), `rate_bps` (1800), `applies_to` (PRODUCT or PLAN or ALL), `is_active`, `effective_from`

**`discounts`**: discount rules, admin-only writes.
- `id`, `code`, `name`, `discount_type` (FIXED, PERCENT), `value` (paise for FIXED, bps for PERCENT)
- `scope` (PRODUCT, SUBSCRIPTION), `product_id` (required when scope is PRODUCT)
- `min_amount_paise`, `min_quantity` (both nullable)
- `starts_at`, `ends_at` (TIMESTAMPTZ)
- `usage_limit` (NULL means unlimited), `is_active`, `version`
- CHECK: PERCENT value between 1 and 10000 bps.

**`discount_redemptions`**: one row each time a discount is applied to an invoice. The source of truth for usage.
- `id`, `discount_id`, `invoice_id`, `subscription_id`, `amount_paise`, `redeemed_at`
- UNIQUE(`discount_id`, `invoice_id`) so the same discount can't apply twice to one invoice.
- Usage count = `COUNT(*)` of rows for a discount. It is never stored as a counter.

### 2.5 Subscriptions

**`subscriptions`**: a customer's active agreement.
- `id`, `number` (UNIQUE, for example "SUB-2026-000123"), `customer_id`, `plan_version_id` (the agreed version, never the plan)
- `status` (DRAFT, QUOTATION, CONFIRMED, ACTIVE, CLOSED)
- `is_paused` BOOLEAN, `start_date`, `expiry_date`, `payment_terms_days`
- `next_billing_date` (indexed, drives the billing run)
- `quotation_valid_until` (set when the status is QUOTATION)
- `close_reason` (nullable)
- `created_by`, `version`, timestamps

**`subscription_lines`**: the products and quantities on the agreement.
- `id`, `subscription_id`, `product_id`, `variant_id` (nullable), `quantity`
- `unit_price_paise` (snapshot at agreement time, never re-read from the catalog)
- `tax_rule_id` (nullable), `line_total_paise`

Rule: unit prices are copied in when the subscription is confirmed. Later catalog or plan price changes never affect existing subscriptions.

### 2.6 Invoicing

**`invoices`**: billing documents, one per subscription per billing period.
- `id`, `number` (UNIQUE, for example "INV-2026-000045"), `subscription_id`, `customer_id` (copied for fast lookups)
- `status` (DRAFT, CONFIRMED, PAID, CANCELLED)
- `period_start`, `period_end`, `issue_date`, `due_date`
- `currency` CHAR(3) default 'INR'
- `subtotal_paise`, `discount_paise`, `tax_paise`, `total_paise`, `amount_paid_paise`
- `version`, timestamps
- **UNIQUE(`subscription_id`, `period_start`)**: the main guard against double billing
- CHECK: `total_paise >= 0`, `amount_paid_paise <= total_paise` or overpayments tracked separately.

**`invoice_lines`**: what the invoice charges for.
- `id`, `invoice_id`, `product_id`, `variant_id`, `description`, `quantity`, `unit_price_paise`
- `discount_paise`, `tax_rule_id`, `tax_rate_bps` (snapshot), `tax_paise`, `line_total_paise`
- Tax rate is copied in, so a later rule change doesn't alter old invoices.

**`invoice_sequences`**: a gap-free counter per type and year.
- `sequence_key` (for example "INV-2026"), `next_value`
- Incremented with `SELECT … FOR UPDATE`. Never use a Postgres `SEQUENCE` for invoice numbers if gaps must be avoided, since sequences don't roll back. (Use a sequence only where gaps are acceptable.)

### 2.7 Payments

**`payments`**: money received against invoices.
- `id`, `invoice_id`, `method` (CASH, BANK_TRANSFER, CARD, UPI, CHEQUE, OTHER), `amount_paise` (CHECK > 0)
- `paid_on` (DATE), `reference` (transaction ID or cheque number), `note`
- `recorded_by` (user id), `created_at`
- Payments are never edited or deleted. A wrong payment is reversed with a negative-amount entry, or with a status column if reversals are added later. For the hackathon, no reversal flow.
- After each insert, the invoice's `amount_paid_paise` is updated in the same transaction, and the status changes to PAID when fully covered.

### 2.8 Background jobs and outbox

**`job_runs`**: one row per billing run or other batch job.
- `id`, `job_type` (BILLING, EMAIL_SEND), `run_for_date`, `status` (RUNNING, COMPLETED, FAILED), `created_count`, `skipped_count`, `failed_count`, `started_at`, `finished_at`, `triggered_by`
- Gives the admin a record of every run and its outcome.

**`email_outbox`**: emails waiting to be sent. Written in the same transaction as the business change.
- `id`, `to_address`, `subject`, `body`, `template_key`, `status` (PENDING, SENT, FAILED), `attempts`, `next_attempt_at`, `last_error`, `created_at`, `sent_at`
- A worker polls PENDING rows and sends them. This keeps SMTP out of request handling.

### 2.9 Support and chatbot

**`faq_articles`**: answers the bot can give without AI.
- `id`, `question`, `answer`, `keywords` (TEXT[]), `category`, `is_published`, `updated_at`

**`chat_sessions`**: one conversation between a customer and the bot or an agent.
- `id`, `user_id`, `customer_id`, `status` (BOT, ESCALATED, CLOSED), `started_at`, `ended_at`, `ticket_id` (nullable)

**`chat_messages`**: each message in a session.
- `id`, `session_id`, `sender` (CUSTOMER, BOT, AGENT), `body`, `intent` (nullable, for analytics), `created_at`
- Index on (`session_id`, `created_at`) for loading a conversation.

**`support_tickets`**: escalated issues for customer care.
- `id`, `number` (UNIQUE, for example "TKT-000381"), `customer_id`, `session_id` (nullable), `subject`
- `status` (OPEN, IN_PROGRESS, RESOLVED), `priority` (LOW, NORMAL, HIGH), `assigned_staff_id` (nullable)
- `created_at`, `resolved_at`, `version`

**`ticket_comments`**: staff replies and internal notes.
- `id`, `ticket_id`, `author_id`, `body`, `is_internal` BOOLEAN (internal notes are hidden from customers), `created_at`

### 2.10 Audit

**`audit_logs`**: records every change to money or permissions.
- `id`, `actor_id`, `actor_role`, `action` (for example "PLAN_VERSION_PUBLISHED", "DISCOUNT_UPDATED", "INVOICE_CANCELLED"), `entity_type`, `entity_id`
- `before_json`, `after_json` (JSONB), `ip_address`, `created_at`
- Append-only. The application user has no UPDATE or DELETE grant on this table.

## 3. Relationships

```
users 1──1 customers
customers 1──* subscriptions
products 1──* product_variants
products 1──* plans ──1──* plan_versions ──1──* plan_limits
plan_versions 1──* subscriptions
subscriptions 1──* subscription_lines
subscriptions 1──* invoices
invoices 1──* invoice_lines
invoices 1──* payments
invoices 1──* discount_redemptions *──1 discounts
customers 1──* chat_sessions 1──* chat_messages
chat_sessions 0..1──1 support_tickets 1──* ticket_comments
```

## 4. Key queries the database must answer fast

| Question | Supporting index |
|---|---|
| Which subscriptions are due for billing today? | `subscriptions(status, next_billing_date)` |
| Has this period already been billed? | UNIQUE `invoices(subscription_id, period_start)` |
| Which invoices are overdue? | `invoices(status, due_date)` partial index WHERE status = 'CONFIRMED' |
| How much is outstanding? | `invoices(status)` plus `amount_paid_paise` |
| Payments for an invoice | `payments(invoice_id)` |
| Discount usage | `discount_redemptions(discount_id)` |
| Customer's own subscriptions | `subscriptions(customer_id, status)` |
| Chat history | `chat_messages(session_id, created_at)` |
| Next outbox emails | `email_outbox(status, next_attempt_at)` partial index WHERE status = 'PENDING' |

## 5. Rules enforced in the database, not only in code

- UNIQUE on `users.email`, `invoices(subscription_id, period_start)`, `discount_redemptions(discount_id, invoice_id)`, `plan_versions(plan_id, version_no)`, `plan_limits(plan_version_id, limit_key)`
- CHECK on enum columns, `payments.amount_paise > 0`, `discounts` percent range, `invoices.total_paise >= 0`
- Foreign keys everywhere, with `ON DELETE RESTRICT` for financial tables. Nothing financial is deleted by cascade.
- `NOT NULL` on every column except those explicitly marked optional above.

## 6. Seed data plan

| Data | Volume | Purpose |
|---|---|---|
| Users | 1 admin, 2 staff, 50 customers | Role testing |
| Products and variants | 10 products, 3 variants each | Catalog screens |
| Plans and versions | 3 plans, 2 versions for one plan | Versioning demo |
| Subscriptions | 5,000 spread over statuses | Performance target |
| Invoices and payments | About 5,000 invoices, mixed PAID, CONFIRMED, and overdue | Dashboard and aging |
| Discounts | 4 rules, some expired, some exhausted | Discount demo |
| FAQ articles | 20 | Chatbot answers |
| Tickets | 30 across all statuses | Support queue |

## 7. Flyway migration order

| File | Contents |
|---|---|
| V1__identity.sql | users, password_resets, customers |
| V2__catalog.sql | products, product_variants |
| V3__plans.sql | plans, plan_versions, plan_limits, quotation_templates |
| V4__tax_discounts.sql | tax_rules, discounts, discount_redemptions |
| V5__subscriptions.sql | subscriptions, subscription_lines |
| V6__billing.sql | invoices, invoice_lines, invoice_sequences, payments |
| V7__jobs_outbox.sql | job_runs, email_outbox |
| V8__support.sql | faq_articles, chat_sessions, chat_messages, support_tickets, ticket_comments |
| V9__audit_indexes.sql | audit_logs, and the performance indexes from section 4 |

## 8. Confirmed Defaults
1. **Currency:** INR default (`currency CHAR(3) DEFAULT 'INR'`).
2. **GST:** Single rate snapshot on lines (`tax_rate_bps`, `tax_paise`).
3. **Overpayments:** Strictly rejected via `CHECK (amount_paid_paise <= total_paise)`.
4. **Payment Reversals:** Out of scope for hackathon (recorded payments are append-only).
