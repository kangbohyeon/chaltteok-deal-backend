package com.chaltteok.user.coupon.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class CouponErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    COUPON_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 쿠폰입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 쿠폰입니다."),
    COUPON_EXHAUSTED(HttpStatus.BAD_REQUEST, "쿠폰 수량이 소진되었습니다."),
    COUPON_MIN_ORDER_NOT_MET(HttpStatus.BAD_REQUEST, "최소 주문금액을 충족하지 않습니다."),
}
