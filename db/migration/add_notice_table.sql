CREATE TABLE IF NOT EXISTS tb_notice (
    notice_id   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    notice_uuid VARCHAR(36)  NOT NULL,
    title       VARCHAR(200) NOT NULL,
    content     TEXT         NOT NULL,
    is_visible  TINYINT(1)   NOT NULL DEFAULT 1,
    location    VARCHAR(50),
    start_date  DATE,
    end_date    DATE,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,
    CONSTRAINT uk_notice_uuid UNIQUE (notice_uuid)
);
