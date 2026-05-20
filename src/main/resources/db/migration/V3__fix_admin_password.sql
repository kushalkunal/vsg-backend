-- V3: Fix admin user password hash
-- Password is 'Admin@1234' — previous hash in V2 was incorrect
UPDATE app_users
SET password_hash = '$2a$10$n/satQRQ3E1E3FtcZreVfef88ibO1b0GOFrv22/C.lAQXjyVe39RG'
WHERE email = 'admin@vsg.com';
