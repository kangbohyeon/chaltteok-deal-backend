ALTER TABLE tb_notifications
    ADD COLUMN IF NOT EXISTS order_number VARCHAR(50) NULL;
