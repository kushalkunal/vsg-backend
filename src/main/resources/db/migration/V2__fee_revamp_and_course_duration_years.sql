-- V2: Revamp fee structure (remove visa/insurance, add registration/examination/total packages)
--     and rename course duration from months to years

-- ============================================================
-- FEES: remove old columns
-- ============================================================
ALTER TABLE fees
    DROP COLUMN IF EXISTS visa_fee,
    DROP COLUMN IF EXISTS insurance_fee;

-- ============================================================
-- FEES: add new columns
-- ============================================================
ALTER TABLE fees
    ADD COLUMN IF NOT EXISTS registration_fee         NUMERIC(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS examination_fee          NUMERIC(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS total_pkg_without_hostel NUMERIC(15,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS total_pkg_with_hostel    NUMERIC(15,2) NOT NULL DEFAULT 0;

-- ============================================================
-- COURSES: rename duration_months -> duration_years
-- ============================================================
ALTER TABLE courses RENAME COLUMN duration_months TO duration_years;
