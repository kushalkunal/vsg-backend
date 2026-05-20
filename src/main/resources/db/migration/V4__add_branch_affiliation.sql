-- V4: Add branch to fees, state + affiliation to colleges

-- Allow branch-level fee variation (e.g. B.Tech CSE vs B.Tech ECE at same college)
ALTER TABLE fees ADD COLUMN IF NOT EXISTS branch VARCHAR(100);

-- College state and affiliation for richer catalog display
ALTER TABLE colleges ADD COLUMN IF NOT EXISTS state VARCHAR(100);
ALTER TABLE colleges ADD COLUMN IF NOT EXISTS affiliation VARCHAR(255);

-- Indexes for admission search queries
CREATE INDEX IF NOT EXISTS idx_fees_branch    ON fees(tenant_id, branch);
CREATE INDEX IF NOT EXISTS idx_fees_course    ON fees(tenant_id, course_id);
CREATE INDEX IF NOT EXISTS idx_colleges_city  ON colleges(tenant_id, city);
CREATE INDEX IF NOT EXISTS idx_colleges_state ON colleges(tenant_id, state);
