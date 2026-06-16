-- tb_products 테이블에 노출 여부, 품절 여부, 추천 여부 컬럼 추가
ALTER TABLE tb_products
    ADD COLUMN IF NOT EXISTS is_sold_out    TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS is_recommended TINYINT(1) NOT NULL DEFAULT 0;
