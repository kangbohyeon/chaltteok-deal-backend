-- tb_notice에 시간 필드 추가
ALTER TABLE tb_notice
    ADD COLUMN IF NOT EXISTS start_time TIME NULL,
    ADD COLUMN IF NOT EXISTS end_time TIME NULL;

-- 댓글 테이블 생성
CREATE TABLE IF NOT EXISTS tb_comment (
    comment_id      BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    comment_uuid    VARCHAR(36)  NOT NULL,
    product_id      BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    content         TEXT         NOT NULL,
    rating          TINYINT      NULL,
    is_secret       TINYINT(1)   NOT NULL DEFAULT 0,
    parent_id       BIGINT       NULL,
    is_owner_reply  TINYINT(1)   NOT NULL DEFAULT 0,
    created_at      DATETIME     NOT NULL,
    updated_at      DATETIME     NOT NULL,
    CONSTRAINT uk_comment_uuid UNIQUE (comment_uuid),
    CONSTRAINT fk_comment_product FOREIGN KEY (product_id) REFERENCES tb_products(product_id)
);

CREATE INDEX IF NOT EXISTS idx_comment_product ON tb_comment (product_id);
CREATE INDEX IF NOT EXISTS idx_comment_parent  ON tb_comment (parent_id);
