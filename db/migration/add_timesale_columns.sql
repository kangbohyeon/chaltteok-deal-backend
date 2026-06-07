ALTER TABLE tb_daily_stocks
    ADD COLUMN start_at DATETIME NULL COMMENT '타임세일 시작 시각',
    ADD COLUMN end_at DATETIME NULL COMMENT '타임세일 종료 시각',
    ADD COLUMN max_purchase_count INT NOT NULL DEFAULT 1 COMMENT '1인 최대 구매 횟수';

UPDATE tb_daily_stocks SET start_at = NULL, end_at = NULL WHERE stock_type = 'NORMAL';

CREATE INDEX idx_end_at_status ON tb_daily_stocks (end_at, status);
