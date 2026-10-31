-- R__seed_plans.sql
-- Seed versioned plans and limits for product ODOO-BASE

-- 1. Insert Plans
INSERT INTO plans (code, name, product_id, status)
SELECT 'STARTER', 'Starter Plan', p.id, 'PUBLISHED'
FROM products p WHERE p.sku = 'ODOO-BASE'
ON CONFLICT (code) DO NOTHING;

INSERT INTO plans (code, name, product_id, status)
SELECT 'GROWTH', 'Growth Plan', p.id, 'PUBLISHED'
FROM products p WHERE p.sku = 'ODOO-BASE'
ON CONFLICT (code) DO NOTHING;

INSERT INTO plans (code, name, product_id, status)
SELECT 'ENTERPRISE', 'Enterprise Plan', p.id, 'PUBLISHED'
FROM products p WHERE p.sku = 'ODOO-BASE'
ON CONFLICT (code) DO NOTHING;

-- 2. Insert Plan Versions
-- Plan STARTER v1: 49900 paise, MONTHLY, min_quantity 1, PUBLISHED
INSERT INTO plan_versions (
    plan_id, version_no, status, price_paise, billing_cycle, min_quantity,
    available_from, available_to, auto_close, allow_early_close, allow_pause, allow_renewal,
    effective_from, effective_to, created_by
)
SELECT p.id, 1, 'PUBLISHED', 49900, 'MONTHLY', 1,
       CURRENT_DATE - INTERVAL '30 days', NULL, FALSE, FALSE, FALSE, TRUE,
       CURRENT_TIMESTAMP - INTERVAL '30 days', NULL, u.id
FROM plans p, users u
WHERE p.code = 'STARTER' AND u.email = 'admin@brightloop.test'
ON CONFLICT (plan_id, version_no) DO NOTHING;

-- Plan GROWTH v1: 149900 paise (RETIRED, effective_to set in the past)
INSERT INTO plan_versions (
    plan_id, version_no, status, price_paise, billing_cycle, min_quantity,
    available_from, available_to, auto_close, allow_early_close, allow_pause, allow_renewal,
    effective_from, effective_to, created_by
)
SELECT p.id, 1, 'RETIRED', 149900, 'MONTHLY', 1,
       CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE - INTERVAL '1 day', FALSE, FALSE, FALSE, TRUE,
       CURRENT_TIMESTAMP - INTERVAL '60 days', CURRENT_TIMESTAMP - INTERVAL '1 day', u.id
FROM plans p, users u
WHERE p.code = 'GROWTH' AND u.email = 'admin@brightloop.test'
ON CONFLICT (plan_id, version_no) DO NOTHING;

-- Plan GROWTH v2: 159900 paise (PUBLISHED)
INSERT INTO plan_versions (
    plan_id, version_no, status, price_paise, billing_cycle, min_quantity,
    available_from, available_to, auto_close, allow_early_close, allow_pause, allow_renewal,
    effective_from, effective_to, created_by
)
SELECT p.id, 2, 'PUBLISHED', 159900, 'MONTHLY', 1,
       CURRENT_DATE - INTERVAL '1 day', NULL, FALSE, FALSE, FALSE, TRUE,
       CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, u.id
FROM plans p, users u
WHERE p.code = 'GROWTH' AND u.email = 'admin@brightloop.test'
ON CONFLICT (plan_id, version_no) DO NOTHING;

-- Plan ENTERPRISE v1: 499900 paise, YEARLY, PUBLISHED
INSERT INTO plan_versions (
    plan_id, version_no, status, price_paise, billing_cycle, min_quantity,
    available_from, available_to, auto_close, allow_early_close, allow_pause, allow_renewal,
    effective_from, effective_to, created_by
)
SELECT p.id, 1, 'PUBLISHED', 499900, 'YEARLY', 1,
       CURRENT_DATE - INTERVAL '30 days', NULL, FALSE, FALSE, FALSE, TRUE,
       CURRENT_TIMESTAMP - INTERVAL '30 days', NULL, u.id
FROM plans p, users u
WHERE p.code = 'ENTERPRISE' AND u.email = 'admin@brightloop.test'
ON CONFLICT (plan_id, version_no) DO NOTHING;

-- 3. Insert Plan Limits
-- STARTER v1: USERS=3, STORAGE_GB=10, CUSTOM_DOMAINS=0
INSERT INTO plan_limits (plan_version_id, limit_key, limit_value)
SELECT pv.id, 'USERS', 3
FROM plan_versions pv JOIN plans p ON pv.plan_id = p.id
WHERE p.code = 'STARTER' AND pv.version_no = 1
ON CONFLICT (plan_version_id, limit_key) DO NOTHING;

INSERT INTO plan_limits (plan_version_id, limit_key, limit_value)
SELECT pv.id, 'STORAGE_GB', 10
FROM plan_versions pv JOIN plans p ON pv.plan_id = p.id
WHERE p.code = 'STARTER' AND pv.version_no = 1
ON CONFLICT (plan_version_id, limit_key) DO NOTHING;

INSERT INTO plan_limits (plan_version_id, limit_key, limit_value)
SELECT pv.id, 'CUSTOM_DOMAINS', 0
FROM plan_versions pv JOIN plans p ON pv.plan_id = p.id
WHERE p.code = 'STARTER' AND pv.version_no = 1
ON CONFLICT (plan_version_id, limit_key) DO NOTHING;

-- GROWTH v2: USERS=15, STORAGE_GB=100, CUSTOM_DOMAINS=1
INSERT INTO plan_limits (plan_version_id, limit_key, limit_value)
SELECT pv.id, 'USERS', 15
FROM plan_versions pv JOIN plans p ON pv.plan_id = p.id
WHERE p.code = 'GROWTH' AND pv.version_no = 2
ON CONFLICT (plan_version_id, limit_key) DO NOTHING;

INSERT INTO plan_limits (plan_version_id, limit_key, limit_value)
SELECT pv.id, 'STORAGE_GB', 100
FROM plan_versions pv JOIN plans p ON pv.plan_id = p.id
WHERE p.code = 'GROWTH' AND pv.version_no = 2
ON CONFLICT (plan_version_id, limit_key) DO NOTHING;

INSERT INTO plan_limits (plan_version_id, limit_key, limit_value)
SELECT pv.id, 'CUSTOM_DOMAINS', 1
FROM plan_versions pv JOIN plans p ON pv.plan_id = p.id
WHERE p.code = 'GROWTH' AND pv.version_no = 2
ON CONFLICT (plan_version_id, limit_key) DO NOTHING;

-- ENTERPRISE v1: USERS=NULL (unlimited), STORAGE_GB=1024, CUSTOM_DOMAINS=1
INSERT INTO plan_limits (plan_version_id, limit_key, limit_value)
SELECT pv.id, 'USERS', NULL
FROM plan_versions pv JOIN plans p ON pv.plan_id = p.id
WHERE p.code = 'ENTERPRISE' AND pv.version_no = 1
ON CONFLICT (plan_version_id, limit_key) DO NOTHING;

INSERT INTO plan_limits (plan_version_id, limit_key, limit_value)
SELECT pv.id, 'STORAGE_GB', 1024
FROM plan_versions pv JOIN plans p ON pv.plan_id = p.id
WHERE p.code = 'ENTERPRISE' AND pv.version_no = 1
ON CONFLICT (plan_version_id, limit_key) DO NOTHING;

INSERT INTO plan_limits (plan_version_id, limit_key, limit_value)
SELECT pv.id, 'CUSTOM_DOMAINS', 1
FROM plan_versions pv JOIN plans p ON pv.plan_id = p.id
WHERE p.code = 'ENTERPRISE' AND pv.version_no = 1
ON CONFLICT (plan_version_id, limit_key) DO NOTHING;

-- 4. Update current_version_id after inserts
UPDATE plans
SET current_version_id = (
    SELECT pv.id
    FROM plan_versions pv
    WHERE pv.plan_id = plans.id
      AND pv.effective_to IS NULL
      AND pv.status = 'PUBLISHED'
    ORDER BY pv.version_no DESC
    LIMIT 1
)
WHERE code IN ('STARTER', 'GROWTH', 'ENTERPRISE');
