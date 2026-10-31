-- V10__plans_versioning.sql
-- Add missing columns, constraints, and indexes for plan versioning

-- 1. Add status column to plan_versions
ALTER TABLE plan_versions
    ADD COLUMN IF NOT EXISTS status VARCHAR(50) NOT NULL DEFAULT 'DRAFT'
    CHECK (status IN ('DRAFT', 'PUBLISHED', 'RETIRED'));

-- 2. Update price_paise constraint to strictly positive (> 0)
ALTER TABLE plan_versions
    DROP CONSTRAINT IF EXISTS plan_versions_price_paise_check;
ALTER TABLE plan_versions
    ADD CONSTRAINT chk_plan_versions_price_positive CHECK (price_paise > 0);

-- 3. Add constraint that available_to >= available_from when specified
ALTER TABLE plan_versions
    DROP CONSTRAINT IF EXISTS chk_plan_versions_availability;
ALTER TABLE plan_versions
    ADD CONSTRAINT chk_plan_versions_availability CHECK (available_to IS NULL OR available_to >= available_from);

-- 4. Add limit_key check constraint on plan_limits
ALTER TABLE plan_limits
    DROP CONSTRAINT IF EXISTS chk_plan_limits_key;
ALTER TABLE plan_limits
    ADD CONSTRAINT chk_plan_limits_key CHECK (limit_key IN ('USERS', 'STORAGE_GB', 'API_CALLS', 'CUSTOM_DOMAINS'));

-- 5. Partial unique index: only one published version per plan is active (effective_to IS NULL)
CREATE UNIQUE INDEX IF NOT EXISTS uq_plan_versions_active ON plan_versions (plan_id)
    WHERE effective_to IS NULL AND status = 'PUBLISHED';

-- 6. Indexes on foreign keys
CREATE INDEX IF NOT EXISTS idx_plan_versions_plan_id ON plan_versions(plan_id);
CREATE INDEX IF NOT EXISTS idx_plan_limits_plan_version_id ON plan_limits(plan_version_id);
