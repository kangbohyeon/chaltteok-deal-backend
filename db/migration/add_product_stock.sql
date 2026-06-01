-- tb_products 테이블에 일별 재고 수량 컬럼 추가
ALTER TABLE tb_products
    ADD COLUMN IF NOT EXISTS stock_quantity INT NULL COMMENT '일별 재고 설정 수량 (null=무제한)',
    ADD COLUMN IF NOT EXISTS current_stock  INT NULL COMMENT '오늘 남은 재고 (null=무제한)';
