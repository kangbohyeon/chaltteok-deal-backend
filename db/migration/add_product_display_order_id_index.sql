CREATE INDEX IF NOT EXISTS idx_products_display_order_id
    ON tb_products (display_order, id);
