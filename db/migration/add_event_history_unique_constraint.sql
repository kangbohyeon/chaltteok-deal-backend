-- tb_event_history에 (user_id, stock_id) 복합 UniqueConstraint 추가
-- NonUniqueResultException 방지: 동일 유저가 동일 타임세일에 중복 이력 생성 불가
ALTER TABLE tb_event_history
    ADD CONSTRAINT uk_event_history_user_stock UNIQUE (user_id, stock_id);
