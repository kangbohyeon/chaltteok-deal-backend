package com.chaltteok.owner.coupon.dto

import com.chaltteok.core.domain.Coupon

data class CouponResponse(
    val couponUuid: String,
    val code: String,
    val name: String,
    val discountType: String,
    val discountValue: Int,
    val minOrderAmount: Int?,
    val maxDiscountAmount: Int?,
    val totalQuantity: Int?,
    val usedQuantity: Int,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean,
    val createdAt: String,
) {
    companion object {
        fun from(coupon: Coupon) = CouponResponse(
            couponUuid = coupon.couponUuid,
            code = coupon.code,
            name = coupon.name,
            discountType = coupon.discountType.name,
            discountValue = coupon.discountValue,
            minOrderAmount = coupon.minOrderAmount,
            maxDiscountAmount = coupon.maxDiscountAmount,
            totalQuantity = coupon.totalQuantity,
            usedQuantity = coupon.usedQuantity,
            startDate = coupon.startDate.toString(),
            endDate = coupon.endDate.toString(),
            isActive = coupon.isActive,
            createdAt = coupon.createdAt.toString(),
        )
    }
}
