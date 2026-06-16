-- countNewUsers 쿼리: tb_users.created_at BETWEEN 범위 조회 성능 개선
ALTER TABLE tb_users
ADD INDEX idx_users_created_at (created_at);
