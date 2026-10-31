-- V10__reminders_and_email_attachment.sql
-- Subscriptions reminders tracking and email outbox attachment reference

CREATE TABLE subscription_reminders (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    subscription_id BIGINT NOT NULL REFERENCES subscriptions(id) ON DELETE RESTRICT,
    days_before INTEGER NOT NULL,
    sent_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_subscription_reminders UNIQUE (subscription_id, days_before)
);

ALTER TABLE email_outbox
    ADD COLUMN attachment_invoice_id BIGINT REFERENCES invoices(id) ON DELETE SET NULL,
    ADD COLUMN attachment_name VARCHAR(255);
