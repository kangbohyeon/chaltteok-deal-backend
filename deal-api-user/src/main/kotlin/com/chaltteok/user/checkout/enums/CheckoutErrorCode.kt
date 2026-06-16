package com.chaltteok.user.checkout.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class CheckoutErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    EMPTY_CART(HttpStatus.BAD_REQUEST, "주문 상품이 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),
    STOCK_LOCK_FAILED(HttpStatus.CONFLICT, "일시적으로 주문이 집중되어 잠시 후 다시 시도해주세요."),
}
