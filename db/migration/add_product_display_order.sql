ALTER TABLE tb_products
    ADD COLUMN IF NOT EXISTS display_order INT NOT NULL DEFAULT 0 COMMENT '노출 우선순위 (낮을수록 먼저 표시)';

CREATE INDEX IF NOT EXISTS idx_products_active_order_name
    ON tb_products (is_active, display_order, name);
