-- V3__plans.sql
-- Plans: plans, plan_versions, plan_limits, quotation_templates

CREATE TABLE plans (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED', 'RETIRED')),
    current_version_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_plans_code UNIQUE (code)
);

CREATE TABLE plan_versions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    plan_id BIGINT NOT NULL REFERENCES plans(id) ON DELETE RESTRICT,
    version_no INTEGER NOT NULL,
    price_paise BIGINT NOT NULL CHECK (price_paise >= 0),
    billing_cycle VARCHAR(50) NOT NULL CHECK (billing_cycle IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),
    min_quantity INTEGER NOT NULL DEFAULT 1 CHECK (min_quantity > 0),
    available_from DATE NOT NULL,
    available_to DATE,
    auto_close BOOLEAN NOT NULL DEFAULT FALSE,
    allow_early_close BOOLEAN NOT NULL DEFAULT FALSE,
    allow_pause BOOLEAN NOT NULL DEFAULT FALSE,
    allow_renewal BOOLEAN NOT NULL DEFAULT TRUE,
    effective_from TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    effective_to TIMESTAMPTZ,
    created_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_plan_versions_plan_version_no UNIQUE (plan_id, version_no)
);

-- Add foreign key constraint for current_version_id now that plan_versions exists
ALTER TABLE plans
    ADD CONSTRAINT fk_plans_current_version
    FOREIGN KEY (current_version_id)
    REFERENCES plan_versions(id)
    ON DELETE RESTRICT;

CREATE TABLE plan_limits (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    plan_version_id BIGINT NOT NULL REFERENCES plan_versions(id) ON DELETE RESTRICT,
    limit_key VARCHAR(100) NOT NULL,
    limit_value BIGINT,
    CONSTRAINT uq_plan_limits_version_key UNIQUE (plan_version_id, limit_key)
);

CREATE TABLE quotation_templates (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    plan_version_id BIGINT NOT NULL REFERENCES plan_versions(id) ON DELETE RESTRICT,
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
    valid_days INTEGER NOT NULL DEFAULT 30 CHECK (valid_days > 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
