-- V1: Baseline schema — all migrations (V1–V5) merged into a single zero-reference point

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
-- USERS  (is_active from V5)
-- ============================================================
CREATE TABLE IF NOT EXISTS app_users (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     VARCHAR(50)  NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name          VARCHAR(255),
    roles         TEXT[]       NOT NULL DEFAULT ARRAY['COUNSELLOR'],
    permissions   TEXT[],
    is_active     BOOLEAN      NOT NULL DEFAULT true,
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
-- COLLEGES  (state + affiliation from V4)
-- ============================================================
CREATE TABLE IF NOT EXISTS colleges (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        VARCHAR(50)  NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name             VARCHAR(255) NOT NULL,
    country          VARCHAR(100) NOT NULL,
    city             VARCHAR(100),
    state            VARCHAR(100),
    affiliation      VARCHAR(255),
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
CREATE INDEX IF NOT EXISTS idx_colleges_city    ON colleges(tenant_id, city);
CREATE INDEX IF NOT EXISTS idx_colleges_state   ON colleges(tenant_id, state);

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
-- FEES  (branch from V4)
-- ============================================================
CREATE TABLE IF NOT EXISTS fees (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         VARCHAR(50)  NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    college_id        UUID         REFERENCES colleges(id) ON DELETE SET NULL,
    course_id         UUID         REFERENCES courses(id)  ON DELETE SET NULL,
    branch            VARCHAR(100),
    tuition_fee       NUMERIC(15,2) NOT NULL DEFAULT 0,
    hostel_fee        NUMERIC(15,2) NOT NULL DEFAULT 0,
    visa_fee          NUMERIC(15,2) NOT NULL DEFAULT 0,
    insurance_fee     NUMERIC(15,2) NOT NULL DEFAULT 0,
    miscellaneous_fee NUMERIC(15,2) NOT NULL DEFAULT 0,
    total_fee         NUMERIC(15,2) NOT NULL DEFAULT 0,
    currency          VARCHAR(10)   NOT NULL DEFAULT 'USD',
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_fees_tenant  ON fees(tenant_id);
CREATE INDEX IF NOT EXISTS idx_fees_branch  ON fees(tenant_id, branch);
CREATE INDEX IF NOT EXISTS idx_fees_course  ON fees(tenant_id, course_id);

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

-- ============================================================
-- OTP TOKENS  (from V5)
-- ============================================================
CREATE TABLE IF NOT EXISTS otp_tokens (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL,
    tenant_id   VARCHAR(50),
    token       VARCHAR(255) NOT NULL,
    type        VARCHAR(30)  NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT false,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_otp_tokens_email_type ON otp_tokens(email, type);

-- ============================================================
-- SEED DATA  (default tenant + admin user, password = Admin@1234)
-- ============================================================
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
    '$2a$10$n/satQRQ3E1E3FtcZreVfef88ibO1b0GOFrv22/C.lAQXjyVe39RG',
    'VSG Admin',
    ARRAY['ADMIN','COUNSELLOR']
) ON CONFLICT (email) DO NOTHING;
