-- EventHistory (user_id, stock_id) 복합 UniqueKey 추가
-- 원인: UniqueKey 누락으로 EventHistoryDuplicateChecker가 중복을 감지하지 못해
--       Consumer에서 NonUniqueResultException 발생

-- 1. 기존 중복 데이터 정리 (최초 history_id만 남기고 나머지 삭제)
DELETE t1
FROM tb_event_history t1
         INNER JOIN tb_event_history t2
                    ON t1.user_id = t2.user_id
                        AND t1.stock_id = t2.stock_id
                        AND t1.history_id > t2.history_id;

-- 2. 복합 UniqueKey 추가
ALTER TABLE tb_event_history
    ADD UNIQUE KEY uk_event_history_user_stock (user_id, stock_id);
