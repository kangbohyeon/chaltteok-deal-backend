CREATE TABLE IF NOT EXISTS tb_notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_uuid VARCHAR(36) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL,
    title VARCHAR(100) NOT NULL,
    message VARCHAR(500) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    INDEX idx_notifications_read_created (is_read, created_at)
);
