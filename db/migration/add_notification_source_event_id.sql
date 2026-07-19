-- SOLD_OUT 알림 중복 방지 — OutboxEvent PK를 기록하여 멱등성 보장 (이슈 #252)
-- MySQL UNIQUE + NULL: NULL 값은 중복으로 처리하지 않으므로 기존 행(NULL) 영향 없음
ALTER TABLE tb_notifications
    ADD COLUMN source_event_id BIGINT NULL,
    ADD UNIQUE KEY uk_notification_source_event (source_event_id);
