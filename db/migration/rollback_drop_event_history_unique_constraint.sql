-- drop_event_history_unique_constraint.sql 롤백용
-- 비유니크 인덱스 제거 후 유니크 제약 복원
DROP INDEX idx_event_history_user_stock ON tb_event_history;

ALTER TABLE tb_event_history
    ADD CONSTRAINT uk_event_history_user_stock UNIQUE (user_id, stock_id);
