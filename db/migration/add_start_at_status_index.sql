-- status 등치 조건을 선두로 배치하여 SCHEDULED 상태 행을 선 필터링 후 start_at 범위 스캔 (이슈 #245)
CREATE INDEX IF NOT EXISTS idx_status_start_at ON tb_time_sale_stocks (status, start_at);
