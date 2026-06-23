CREATE TABLE tb_coupons (
    coupon_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_uuid   VARCHAR(36)  NOT NULL UNIQUE,
    code          VARCHAR(50)  NOT NULL UNIQUE COMMENT '쿠폰 코드',
    name          VARCHAR(100) NOT NULL COMMENT '쿠폰 이름',
    discount_type VARCHAR(10)  NOT NULL COMMENT 'RATE(정률) 또는 AMOUNT(정액)',
    discount_value INT          NOT NULL COMMENT '할인율(%) 또는 할인금액(원)',
    min_order_amount INT        NULL COMMENT '최소 주문금액',
    max_discount_amount INT     NULL COMMENT '최대 할인 한도(정률 쿠폰)',
    total_quantity INT          NULL COMMENT '총 발행 수량 (NULL=무제한)',
    used_quantity  INT          NOT NULL DEFAULT 0 COMMENT '사용된 수량',
    start_date    DATE          NOT NULL,
    end_date      DATE          NOT NULL,
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    version       BIGINT       NOT NULL DEFAULT 0 COMMENT '낙관적 락',
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL
);
