-- tb_event_history (user_id, stock_id) UniqueConstraint 제거
-- maxPurchaseCount > 1인 경우 동일 유저의 복수 구매를 허용하기 위해 제거
-- 중복 구매 방지는 EventHistoryDuplicateChecker의 count 기반 체크로 대체
ALTER TABLE tb_event_history
    DROP INDEX uk_event_history_user_stock;
