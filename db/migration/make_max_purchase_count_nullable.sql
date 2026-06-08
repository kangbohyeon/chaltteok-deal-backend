-- max_purchase_count: null 허용 (null = 무제한)
ALTER TABLE tb_daily_stocks MODIFY COLUMN max_purchase_count INT NULL DEFAULT NULL;

-- EventHistory unique constraint 제거: 복수 구매 지원
ALTER TABLE tb_event_history DROP INDEX uk_one_event_per_user;
