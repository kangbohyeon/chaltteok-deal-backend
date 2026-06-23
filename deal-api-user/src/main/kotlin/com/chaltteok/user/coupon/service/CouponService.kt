package com.chaltteok.user.coupon.service

import com.chaltteok.user.coupon.dto.CouponValidateResponse

interface CouponService {
    fun validate(code: String, orderAmount: Int): CouponValidateResponse
}
