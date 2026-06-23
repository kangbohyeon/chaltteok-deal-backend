package com.chaltteok.user.coupon.dto

data class CouponValidateResponse(
    val couponName: String,
    val discountType: String,
    val discountValue: Int,
    val discountAmount: Int,
    val finalAmount: Int,
)
