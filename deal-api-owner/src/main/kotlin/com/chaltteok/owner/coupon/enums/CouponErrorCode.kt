package com.chaltteok.owner.coupon.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class CouponErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    COUPON_CODE_DUPLICATE(HttpStatus.CONFLICT, "이미 사용 중인 쿠폰 코드입니다."),
}
