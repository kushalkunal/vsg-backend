-- V1: Initial schema for Visionary Salva Group multi-tenant backend

-- ============================================================
-- TENANTS
-- ============================================================
CREATE TABLE IF NOT EXISTS tenants (
    id                VARCHAR(50)  PRIMARY KEY,
    name              VARCHAR(255),
    logo_url          VARCHAR(500),
    primary_color     VARCHAR(20),
    currency          VARCHAR(10)  DEFAULT 'USD',
    countries         TEXT[],
    student_statuses  TEXT[],
    fee_categories    TEXT[],
    dashboard_widgets TEXT[],
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE IF NOT EXISTS app_users (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     VARCHAR(50)  NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name          VARCHAR(255),
    roles         TEXT[]       NOT NULL DEFAULT ARRAY['COUNSELLOR'],
    permissions   TEXT[],
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_users_tenant ON app_users(tenant_id);

-- ============================================================
-- STUDENTS
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id            VARCHAR(50)  NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    full_name            VARCHAR(255) NOT NULL,
    phone                VARCHAR(50)  NOT NULL,
    email                VARCHAR(255),
    interested_course    VARCHAR(255),
    preferred_country    VARCHAR(100),
    budget               NUMERIC(15,2),
    neet_score           INTEGER,
    status               VARCHAR(100) DEFAULT 'New Lead',
    notes                TEXT,
    assigned_counsellor  VARCHAR(255),
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_students_tenant   ON students(tenant_id);
CREATE INDEX IF NOT EXISTS idx_students_status   ON students(tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_students_country  ON students(tenant_id, preferred_country);

-- ============================================================
-- COLLEGES
-- ============================================================
CREATE TABLE IF NOT EXISTS colleges (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        VARCHAR(50)  NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name             VARCHAR(255) NOT NULL,
    country          VARCHAR(100) NOT NULL,
    city             VARCHAR(100),
    ranking          INTEGER,
    description      TEXT,
    nmc_approved     BOOLEAN      NOT NULL DEFAULT FALSE,
    who_approved     BOOLEAN      NOT NULL DEFAULT FALSE,
    hostel_available BOOLEAN      NOT NULL DEFAULT FALSE,
    brochure_url     VARCHAR(500),
    image_url        VARCHAR(500),
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_colleges_tenant  ON colleges(tenant_id);
CREATE INDEX IF NOT EXISTS idx_colleges_country ON colleges(tenant_id, country);

-- ============================================================
-- COURSES
-- ============================================================
CREATE TABLE IF NOT EXISTS courses (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        VARCHAR(50)  NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name             VARCHAR(255) NOT NULL,
    description      TEXT,
    duration_months  INTEGER,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_courses_tenant ON courses(tenant_id);

-- ============================================================
-- FEES
-- ============================================================
CREATE TABLE IF NOT EXISTS fees (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         VARCHAR(50)  NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    college_id        UUID         REFERENCES colleges(id) ON DELETE SET NULL,
    course_id         UUID         REFERENCES courses(id)  ON DELETE SET NULL,
    tuition_fee       NUMERIC(15,2) NOT NULL DEFAULT 0,
    hostel_fee        NUMERIC(15,2) NOT NULL DEFAULT 0,
    visa_fee          NUMERIC(15,2) NOT NULL DEFAULT 0,
    insurance_fee     NUMERIC(15,2) NOT NULL DEFAULT 0,
    miscellaneous_fee NUMERIC(15,2) NOT NULL DEFAULT 0,
    total_fee         NUMERIC(15,2) NOT NULL DEFAULT 0,
    currency          VARCHAR(10)   NOT NULL DEFAULT 'USD',
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_fees_tenant ON fees(tenant_id);

-- ============================================================
-- FOLLOWUPS
-- ============================================================
CREATE TABLE IF NOT EXISTS followups (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     VARCHAR(50)  NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    student_id    UUID         NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    note          TEXT         NOT NULL,
    reminder_date DATE,
    channel       VARCHAR(50),
    created_by    VARCHAR(255),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_followups_tenant  ON followups(tenant_id);
CREATE INDEX IF NOT EXISTS idx_followups_student ON followups(student_id);
