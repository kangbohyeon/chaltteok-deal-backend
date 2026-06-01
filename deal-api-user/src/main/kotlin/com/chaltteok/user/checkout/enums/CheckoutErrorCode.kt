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
}
