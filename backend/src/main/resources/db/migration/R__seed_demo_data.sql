-- Users (password hashes are placeholders; the app's signup/login will create real ones)
INSERT INTO users (email, password_hash, full_name, role)
VALUES
  ('admin@brightloop.test',  '$2a$10$placeholderhashadmin000000000000000000000000000000', 'Admin User',  'ADMIN'),
  ('staff@brightloop.test',  '$2a$10$placeholderhashstaff0000000000000000000000000000000', 'Staff User',  'STAFF'),
  ('cust@brightloop.test',   '$2a$10$placeholderhashcust00000000000000000000000000000000', 'Demo Customer','CUSTOMER')
ON CONFLICT (email) DO NOTHING;

-- Products and variant
INSERT INTO products (sku, name, product_type, sales_price_paise, cost_price_paise, notes)
VALUES ('ODOO-BASE', 'Odoo Business Suite', 'SERVICE', 149900, 80000, 'Demo product')
ON CONFLICT (sku) DO NOTHING;

INSERT INTO product_variants (product_id, name, attribute, price_adder_paise)
SELECT id, 'Odoo', 'brand', 56000 FROM products WHERE sku = 'ODOO-BASE'
ON CONFLICT DO NOTHING;