CREATE TABLE IF NOT EXISTS tb_banner (
    banner_id        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    banner_uuid      VARCHAR(36)  NOT NULL,
    title            VARCHAR(200),
    subtitle         VARCHAR(400),
    image_url        TEXT,
    link_url         TEXT,
    background_color VARCHAR(20),
    sort_order       INT          NOT NULL DEFAULT 0,
    is_visible       TINYINT(1)   NOT NULL DEFAULT 1,
    start_date       DATE,
    end_date         DATE,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    CONSTRAINT uk_banner_uuid UNIQUE (banner_uuid)
);

CREATE INDEX idx_banner_visible_date ON tb_banner (is_visible, start_date, end_date);
