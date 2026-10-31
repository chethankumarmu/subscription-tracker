-- V9__audit_indexes.sql
-- Audit logs and high-performance indexes

CREATE TABLE audit_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    actor_id BIGINT REFERENCES users(id) ON DELETE RESTRICT,
    actor_role VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    before_json JSONB,
    after_json JSONB,
    ip_address VARCHAR(45),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Supporting high-performance indexes from section 4

-- Subscriptions billing due queries & customer lookup
CREATE INDEX idx_subscriptions_status_billing_date ON subscriptions(status, next_billing_date);
CREATE INDEX idx_subscriptions_customer_status ON subscriptions(customer_id, status);
CREATE INDEX idx_subscriptions_expiry_date ON subscriptions(expiry_date);

-- Overdue invoices & status queries
CREATE INDEX idx_invoices_overdue ON invoices(status, due_date) WHERE status = 'CONFIRMED';
CREATE INDEX idx_invoices_status ON invoices(status);

-- Payments per invoice
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);

-- Discount redemption counter lookups
CREATE INDEX idx_discount_redemptions_discount_id ON discount_redemptions(discount_id);

-- Chat history lookups
CREATE INDEX idx_chat_messages_session_created ON chat_messages(session_id, created_at);

-- Pending outbox worker polling
CREATE INDEX idx_email_outbox_pending ON email_outbox(status, next_attempt_at) WHERE status = 'PENDING';
