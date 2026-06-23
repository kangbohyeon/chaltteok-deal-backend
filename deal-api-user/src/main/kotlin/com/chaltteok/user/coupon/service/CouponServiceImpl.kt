package com.chaltteok.user.coupon.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.repository.coupon.CouponRepository
import com.chaltteok.user.coupon.dto.CouponValidateResponse
import com.chaltteok.user.coupon.enums.CouponErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CouponServiceImpl(
    private val couponRepository: CouponRepository,
) : CouponService {

    @Transactional(readOnly = true)
    override fun validate(code: String, orderAmount: Int): CouponValidateResponse {
        val coupon = couponRepository.findByCode(code.uppercase().trim())
            .orElseThrow { BusinessException(CouponErrorCode.COUPON_NOT_FOUND) }

        val today = LocalDate.now()
        if (!coupon.isActive) throw BusinessException(CouponErrorCode.COUPON_INVALID)
        if (today < coupon.startDate || today > coupon.endDate) throw BusinessException(CouponErrorCode.COUPON_EXPIRED)
        val totalQuantity = coupon.totalQuantity
        if (totalQuantity != null && coupon.usedQuantity >= totalQuantity) throw BusinessException(CouponErrorCode.COUPON_EXHAUSTED)
        val minOrderAmount = coupon.minOrderAmount
        if (minOrderAmount != null && orderAmount < minOrderAmount) throw BusinessException(CouponErrorCode.COUPON_MIN_ORDER_NOT_MET)

        val discountAmount = coupon.calculateDiscount(orderAmount)
        return CouponValidateResponse(
            couponName = coupon.name,
            discountType = coupon.discountType.name,
            discountValue = coupon.discountValue,
            discountAmount = discountAmount,
            finalAmount = orderAmount - discountAmount,
        )
    }
}
