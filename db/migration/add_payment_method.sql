-- tb_payments 테이블에 결제 방식 컬럼 추가
-- pgProvider(결제 게이트웨이)와 구분: paymentMethod는 결제 수단 종류(카드, 계좌이체, 간편결제 등)
ALTER TABLE tb_payments
    ADD COLUMN IF NOT EXISTS payment_method VARCHAR(30) NULL;
