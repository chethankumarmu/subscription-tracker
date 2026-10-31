# Person 4: Discounts, Payments & Reporting - Task Breakdown

## 🎯 Role Focus
- Admin-only discount rules engine with concurrent row-level locking.
- Discount redemption ledger tracking.
- Payment recording, multi-payment reconciliation, and invoice auto-settlement.
- High-performance SQL reporting (MRR, ARR, overdue aging analysis).
- Seed dataset generation and query latency benchmarking (< 2.0s).

---

## 📋 Task Checklist

### Phase 1: Discount Engine & Concurrency Locking (Hours 0:40 - 2:30)
- [ ] **T4.1 Admin-Only Discounts Model**
  - `discounts` table: `id`, `code` (UNIQUE), `type` (`FIXED`, `PERCENT`), `value`, `scope` (`PRODUCT`, `SUBSCRIPTION`), `min_amount`, `min_quantity`, `start_date`, `end_date`, `usage_limit`, `is_active`.
  - Enforce `@PreAuthorize("hasRole('ADMIN')")` on all create/update/deactivate endpoints.
  - Staff and customers must receive 403 Forbidden.
- [ ] **T4.2 Redemptions & Concurrency Protection**
  - `discount_redemptions` table: `id`, `invoice_id`, `discount_id`, `amount_applied`, `redeemed_at`.
  - Design rule: NEVER store a mutable counter `discounts.used`. Always compute count via `discount_redemptions`.
  - Locking logic during billing: Execute `SELECT * FROM discounts WHERE id = :id FOR UPDATE` inside the invoice creation transaction.
  - If redemption count exceeds `usage_limit`, reject or skip discount without rolling back other subscriptions.

---

### Phase 2: Payments Engine & Reconciliation (Hours 2:30 - 3:45)
- [ ] **T4.3 Payment Recording**
  - `payments` table: `id`, `invoice_id`, `amount` (> 0 CHECK), `payment_method` (`CARD`, `UPI`, `BANK_TRANSFER`, `CASH`), `transaction_reference`, `recorded_by`, `payment_date`.
  - `POST /api/payments` (Admin and Staff permitted).
- [ ] **T4.4 Partial Payments & Auto-Settlement**
  - Calculate `total_paid = SUM(amount)` for the invoice.
  - If `total_paid >= invoice.total_amount`: Transition invoice status to `PAID`.
  - If `total_paid < invoice.total_amount`: Keep invoice status `CONFIRMED` and record remaining balance.
  - Reject payments on `DRAFT` or `CANCELLED` invoices.

---

### Phase 3: Financial Analytics & SQL Aggregation (Hours 3:45 - 5:00)
- [ ] **T4.5 SQL Dashboard Aggregations (Zero Java-Loop Processing)**
  - `GET /api/reports/finance-dashboard`:
    - **Active Subscriptions:** Count of subscriptions where `status = 'ACTIVE'`.
    - **MRR (Monthly Recurring Revenue):** Normalized SQL calculation:
      ```sql
      SUM(
        CASE
          WHEN pv.billing_cycle = 'MONTHLY' THEN pv.price
          WHEN pv.billing_cycle = 'YEARLY' THEN pv.price / 12
          WHEN pv.billing_cycle = 'WEEKLY' THEN (pv.price * 52) / 12
          WHEN pv.billing_cycle = 'DAILY' THEN (pv.price * 365) / 12
          ELSE 0
        END
      )
      ```
    - **ARR (Annual Recurring Revenue):** `MRR * 12`.
    - **Total Collected:** `SUM(payments.amount)`.
    - **Total Outstanding:** `SUM(invoices.total_amount) - SUM(COALESCE(payments.amount, 0))` on unpaid confirmed invoices.
- [ ] **T4.6 Overdue Invoices Aging Analysis**
  - Derived status: `status = 'CONFIRMED' AND due_date < CURRENT_DATE`.
  - Aging buckets query:
    - 0–30 days overdue
    - 31–60 days overdue
    - 60+ days overdue

---

### Phase 4: Seed Data & Performance Optimization (Hours 5:00 - 6:30)
- [ ] **T4.7 High-Volume Seed Generator**
  - Database seeder script/service:
    - ~5,000 subscriptions across multiple customers, plan versions, and cycles.
    - ~10,000 historical and open invoices.
    - Realistic payments and discount redemptions.
    - 20 FAQ articles with indexed keywords.
- [ ] **T4.8 Index Verification & Performance Tuning**
  - Verify indexes:
    - `CREATE INDEX idx_sub_status_customer ON subscriptions(status, customer_id);`
    - `CREATE INDEX idx_sub_expiry ON subscriptions(expiry_date);`
    - `CREATE INDEX idx_inv_status_due ON invoices(status, due_date);`
    - `CREATE INDEX idx_payments_invoice ON payments(invoice_id);`
    - `CREATE INDEX idx_disc_redemptions ON discount_redemptions(discount_id);`
  - Benchmark all list and dashboard endpoints to guarantee response times under 2.0 seconds with seeded volume.
