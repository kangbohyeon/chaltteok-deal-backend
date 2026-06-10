CREATE TABLE IF NOT EXISTS tb_outbox_events (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    source       VARCHAR(20)  NOT NULL COMMENT 'API_USER | CONSUMER',
    aggregate_id VARCHAR(100) NOT NULL COMMENT '주문번호 등 도메인 식별자',
    event_type   VARCHAR(50)  NOT NULL COMMENT 'ORDER_COMPLETED | ORDER_CANCELLED',
    payload      TEXT         NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING | PROCESSED | FAILED',
    retry_count  INT          NOT NULL DEFAULT 0,
    processed_at DATETIME,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL
);

CREATE INDEX idx_outbox_source_status_created ON tb_outbox_events (source, status, created_at);
