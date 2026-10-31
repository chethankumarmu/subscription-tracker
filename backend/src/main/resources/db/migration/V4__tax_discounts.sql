-- V4__tax_discounts.sql
-- Tax & Discounts: tax_rules, discounts, discount_redemptions

CREATE TABLE tax_rules (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rate_bps INTEGER NOT NULL CHECK (rate_bps >= 0),
    applies_to VARCHAR(50) NOT NULL DEFAULT 'ALL' CHECK (applies_to IN ('PRODUCT', 'PLAN', 'ALL')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    effective_from TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE discounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    discount_type VARCHAR(50) NOT NULL CHECK (discount_type IN ('FIXED', 'PERCENT')),
    value BIGINT NOT NULL CHECK (value > 0),
    scope VARCHAR(50) NOT NULL CHECK (scope IN ('PRODUCT', 'SUBSCRIPTION')),
    product_id BIGINT REFERENCES products(id) ON DELETE RESTRICT,
    min_amount_paise BIGINT CHECK (min_amount_paise IS NULL OR min_amount_paise >= 0),
    min_quantity INTEGER CHECK (min_quantity IS NULL OR min_quantity > 0),
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ,
    usage_limit INTEGER CHECK (usage_limit IS NULL OR usage_limit > 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_discounts_code UNIQUE (code),
    CONSTRAINT chk_discounts_percent_range CHECK (discount_type != 'PERCENT' OR (value >= 1 AND value <= 10000)),
    CONSTRAINT chk_discounts_product_scope CHECK (scope != 'PRODUCT' OR product_id IS NOT NULL)
);

CREATE TABLE discount_redemptions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    discount_id BIGINT NOT NULL REFERENCES discounts(id) ON DELETE RESTRICT,
    invoice_id BIGINT NOT NULL,
    subscription_id BIGINT NOT NULL,
    amount_paise BIGINT NOT NULL CHECK (amount_paise > 0),
    redeemed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_discount_redemptions_discount_invoice UNIQUE (discount_id, invoice_id)
);
