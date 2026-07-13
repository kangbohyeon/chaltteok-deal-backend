ALTER TABLE tb_owner ADD COLUMN withdrawn_at DATETIME(6) NULL DEFAULT NULL;
CREATE INDEX idx_owner_withdrawn_at ON tb_owner (withdrawn_at);
