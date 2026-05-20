-- V2: Seed default tenant and admin user
-- Password is 'Admin@1234' (BCrypt hash generated with strength 10)

INSERT INTO tenants (id, name, currency, student_statuses, countries)
VALUES (
    'vsg-default',
    'Visionary Salva Group',
    'USD',
    ARRAY['New Lead','Contacted','Document Collection','Application Submitted','Visa Approved','Enrolled','Dropped'],
    ARRAY['Russia','Kazakhstan','Kyrgyzstan','Georgia','Philippines','Bangladesh','Ukraine','China']
) ON CONFLICT (id) DO NOTHING;

INSERT INTO app_users (tenant_id, email, password_hash, name, roles)
VALUES (
    'vsg-default',
    'admin@vsg.com',
    '$2a$10$xM1nRMFpJL.hj7f6Ak9D6.DeLJY6yVx3TW0oLTRKf7lKvPE0x7Bqe',
    'VSG Admin',
    ARRAY['ADMIN','COUNSELLOR']
) ON CONFLICT (email) DO NOTHING;
