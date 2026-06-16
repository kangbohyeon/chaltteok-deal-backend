-- ① tb_notice(팝업 용도) → tb_popup 으로 이름 변경 + 컬럼명 정리
RENAME TABLE tb_notice TO tb_popup;

ALTER TABLE tb_popup
    CHANGE COLUMN notice_id popup_id BIGINT NOT NULL AUTO_INCREMENT,
    CHANGE COLUMN notice_uuid popup_uuid VARCHAR(36) NOT NULL;

DROP INDEX uk_notice_uuid ON tb_popup;
CREATE UNIQUE INDEX uk_popup_uuid ON tb_popup (popup_uuid);

-- ② 공지사항 전용 tb_notice 테이블 신규 생성
CREATE TABLE IF NOT EXISTS tb_notice (
    notice_id   BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    notice_uuid VARCHAR(36)  NOT NULL,
    title       VARCHAR(200) NOT NULL,
    content     TEXT         NOT NULL,
    is_visible  TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,
    CONSTRAINT uk_notice_uuid UNIQUE (notice_uuid)
);

-- ③ 1:1 문의 tb_inquiry 테이블 신규 생성
CREATE TABLE IF NOT EXISTS tb_inquiry (
    inquiry_id   BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    inquiry_uuid VARCHAR(36)  NOT NULL,
    user_id      BIGINT       NOT NULL,
    title        VARCHAR(200) NOT NULL,
    content      TEXT         NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    answer       TEXT         NULL,
    answered_at  DATETIME     NULL,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    CONSTRAINT uk_inquiry_uuid UNIQUE (inquiry_uuid)
);

CREATE INDEX idx_inquiry_user ON tb_inquiry (user_id);
