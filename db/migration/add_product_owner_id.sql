-- 상품-점주 소유권 체인 확보 — IDOR 취약점 수정 (이슈 #234)
ALTER TABLE tb_products ADD COLUMN owner_id BIGINT NULL;

-- 기존 상품 백필: 단일 점주 시스템이므로 tb_owner의 첫 번째 레코드로 설정
UPDATE tb_products SET owner_id = (SELECT owner_id FROM tb_owner LIMIT 1) WHERE owner_id IS NULL;

-- 백필 완료 후 NOT NULL 제약, FK 참조 무결성, 인덱스 추가
ALTER TABLE tb_products MODIFY COLUMN owner_id BIGINT NOT NULL;
ALTER TABLE tb_products ADD CONSTRAINT fk_product_owner FOREIGN KEY (owner_id) REFERENCES tb_owner(owner_id) ON DELETE RESTRICT;
ALTER TABLE tb_products ADD INDEX idx_product_owner_id (owner_id);
