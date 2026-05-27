-- V3: Add extra organization approval switches for college records

ALTER TABLE colleges
    ADD COLUMN IF NOT EXISTS ugc_approved BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS aicte_approved BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS naac_accredited BOOLEAN NOT NULL DEFAULT FALSE;
