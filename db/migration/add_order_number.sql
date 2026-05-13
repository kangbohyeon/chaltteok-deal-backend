-- tb_orders 테이블에 사용자 노출용 주문번호 컬럼 추가
-- order_uuid(UUID)는 시스템 내부 식별자, order_number는 사용자 화면 노출용 가독성 식별자
ALTER TABLE tb_orders
    ADD COLUMN IF NOT EXISTS order_number VARCHAR(20) NULL,
    ADD UNIQUE INDEX IF NOT EXISTS uk_order_number (order_number),
    ADD INDEX IF NOT EXISTS idx_order_number (order_number);

-- 기존 데이터에 order_number 채우기 (마이그레이션)
UPDATE tb_orders
SET order_number = CONCAT('ORD', DATE_FORMAT(ordered_at, '%Y%m%d'), UPPER(SUBSTRING(REPLACE(order_uuid, '-', ''), 1, 6)))
WHERE order_number IS NULL;

-- NULL 불허로 변경
ALTER TABLE tb_orders
    MODIFY COLUMN order_number VARCHAR(20) NOT NULL;
