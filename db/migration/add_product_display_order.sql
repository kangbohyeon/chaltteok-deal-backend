ALTER TABLE tb_products
    ADD COLUMN IF NOT EXISTS display_order INT NOT NULL DEFAULT 0 COMMENT '노출 우선순위 (낮을수록 먼저 표시)';
