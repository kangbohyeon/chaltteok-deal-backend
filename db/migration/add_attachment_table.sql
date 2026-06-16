CREATE TABLE IF NOT EXISTS tb_attachment (
    attachment_id   BIGINT       NOT NULL AUTO_INCREMENT,
    attachment_uuid VARCHAR(36)  NOT NULL,
    attachment_type VARCHAR(20),
    reference_uuid  VARCHAR(36),
    file_url        VARCHAR(500) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    created_at      DATETIME     NOT NULL,
    updated_at      DATETIME     NOT NULL,
    CONSTRAINT pk_attachment PRIMARY KEY (attachment_id),
    CONSTRAINT uk_attachment_uuid UNIQUE (attachment_uuid),
    INDEX idx_attachment_reference (reference_uuid, attachment_type)
);
