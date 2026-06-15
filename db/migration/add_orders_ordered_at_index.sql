CREATE INDEX IF NOT EXISTS idx_orders_stats
    ON tb_orders (ordered_at, status, total_price);
