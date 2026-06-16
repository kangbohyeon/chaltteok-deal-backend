-- ordered_at 기반 정렬 쿼리 최적화 인덱스
CREATE INDEX idx_orders_ordered_at ON tb_orders (ordered_at DESC);
