-- Flyway Migration V6: Seed Default Demo Users with Verified BCrypt Hashed Passwords
-- Compatible with PostgreSQL 16 and H2 (PostgreSQL mode)
-- Passwords:
--   admin     : Admin@123
--   regulator : Regulator@123
--   reviewer  : Reviewer@123
--   analyst   : Analyst@123

INSERT INTO users (username, password_hash, email, full_name, role, auth_provider, enabled, created_at)
SELECT 'admin', '$2a$10$uAW1CbW1sO4zbvD2IGdHYO/ubGP9Pi0w/IOHw6Wj/j2xHhRws0a8S', 'admin@consumer.gov.in', 'System Administrator', 'ADMIN', 'LOCAL', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, password_hash, email, full_name, role, auth_provider, enabled, created_at)
SELECT 'regulator', '$2a$10$y9Lm66NkXD3ghS1s98miDumMJqLtuDQyN3kE10/CKsIiCj8DcJf9G', 'regulator@consumer.gov.in', 'Senior Metrology Officer', 'REGULATOR', 'LOCAL', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'regulator');

INSERT INTO users (username, password_hash, email, full_name, role, auth_provider, enabled, created_at)
SELECT 'reviewer', '$2a$10$4te68Y.AnCO8s0DEciuTauxWq.kHmkJTHOtlchkBJbQxNIWL9qjEm', 'reviewer@consumer.gov.in', 'Compliance Review Inspector', 'REVIEWER', 'LOCAL', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'reviewer');

INSERT INTO users (username, password_hash, email, full_name, role, auth_provider, enabled, created_at)
SELECT 'analyst', '$2a$10$pQbwikpxyHgu2hCvOOWNB.17izhoMhVYa1mYpP.EA9Bw.hNaIvPq2', 'analyst@consumer.gov.in', 'Policy & Market Analyst', 'ANALYST', 'LOCAL', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'analyst');
