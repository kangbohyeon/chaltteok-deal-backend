ALTER TABLE tb_users ADD COLUMN withdrawn_at DATETIME(6) NULL DEFAULT NULL;
CREATE INDEX idx_users_withdrawn_at ON tb_users (withdrawn_at);
