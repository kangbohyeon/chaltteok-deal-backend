package com.chaltteok.owner.coupon.service

import com.chaltteok.owner.coupon.dto.CouponRequest
import com.chaltteok.owner.coupon.dto.CouponResponse

interface OwnerCouponService {
    fun getCoupons(): List<CouponResponse>
    fun createCoupon(request: CouponRequest): CouponResponse
    fun updateCoupon(couponUuid: String, request: CouponRequest): CouponResponse
    fun deleteCoupon(couponUuid: String)
    fun toggleActive(couponUuid: String): CouponResponse
}
