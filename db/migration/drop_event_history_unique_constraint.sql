-- tb_event_history (user_id, stock_id) UniqueConstraint 제거 후 비유니크 복합 인덱스로 교체
-- maxPurchaseCount > 1인 경우 동일 유저의 복수 구매를 허용하기 위해 유니크 제거
-- 중복 구매 방지는 PurchaseLimitChecker(count 기반) + OrderConfirmService 이중 검증으로 대체
-- 비유니크 인덱스를 유지하여 countByUser_IdAndTimeSaleStock_Id 쿼리 성능 보장
ALTER TABLE tb_event_history
    DROP INDEX uk_event_history_user_stock;

CREATE INDEX idx_event_history_user_stock
    ON tb_event_history (user_id, stock_id);
