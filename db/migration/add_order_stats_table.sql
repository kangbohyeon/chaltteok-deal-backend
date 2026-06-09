CREATE TABLE IF NOT EXISTS tb_order_stats (
    stat_id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    stat_uuid       VARCHAR(36)  NOT NULL,
    stat_date       DATE         NOT NULL,
    order_count     BIGINT       NOT NULL DEFAULT 0,
    total_revenue   BIGINT       NOT NULL DEFAULT 0,
    cancelled_count BIGINT       NOT NULL DEFAULT 0,
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,
    UNIQUE KEY uk_order_stats_date (stat_date)
);
