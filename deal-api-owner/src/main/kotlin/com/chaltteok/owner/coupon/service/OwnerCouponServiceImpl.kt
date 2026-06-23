package com.chaltteok.owner.coupon.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Coupon
import com.chaltteok.core.repository.coupon.CouponRepository
import com.chaltteok.owner.coupon.dto.CouponRequest
import com.chaltteok.owner.coupon.dto.CouponResponse
import com.chaltteok.owner.coupon.enums.CouponErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerCouponServiceImpl(
    private val couponRepository: CouponRepository,
) : OwnerCouponService {

    @Transactional(readOnly = true)
    override fun getCoupons(): List<CouponResponse> =
        couponRepository.findAllByOrderByCreatedAtDesc().map { CouponResponse.from(it) }

    @Transactional
    override fun createCoupon(request: CouponRequest): CouponResponse {
        if (couponRepository.findByCode(request.code.uppercase().trim()).isPresent) {
            throw BusinessException(CouponErrorCode.COUPON_CODE_DUPLICATE)
        }
        val coupon = couponRepository.save(
            Coupon(
                code = request.code.uppercase().trim(),
                name = request.name,
                discountType = request.discountType,
                discountValue = request.discountValue,
                minOrderAmount = request.minOrderAmount,
                maxDiscountAmount = request.maxDiscountAmount,
                totalQuantity = request.totalQuantity,
                startDate = request.startDate,
                endDate = request.endDate,
                isActive = request.isActive,
            )
        )
        return CouponResponse.from(coupon)
    }

    @Transactional
    override fun updateCoupon(couponUuid: String, request: CouponRequest): CouponResponse {
        val coupon = couponRepository.findByCouponUuid(couponUuid)
            .orElseThrow { BusinessException(CouponErrorCode.COUPON_NOT_FOUND) }
        // code 중복 체크 (자기 자신 제외)
        couponRepository.findByCode(request.code.uppercase().trim())
            .ifPresent { existing ->
                if (existing.couponUuid != couponUuid) throw BusinessException(CouponErrorCode.COUPON_CODE_DUPLICATE)
            }
        // Coupon은 불변 설계이므로 삭제 후 재생성 방식 채택
        couponRepository.delete(coupon)
        val newCoupon = couponRepository.save(
            Coupon(
                code = request.code.uppercase().trim(),
                name = request.name,
                discountType = request.discountType,
                discountValue = request.discountValue,
                minOrderAmount = request.minOrderAmount,
                maxDiscountAmount = request.maxDiscountAmount,
                totalQuantity = request.totalQuantity,
                usedQuantity = coupon.usedQuantity, // 사용 이력 유지
                startDate = request.startDate,
                endDate = request.endDate,
                isActive = request.isActive,
            )
        )
        return CouponResponse.from(newCoupon)
    }

    @Transactional
    override fun deleteCoupon(couponUuid: String) {
        val coupon = couponRepository.findByCouponUuid(couponUuid)
            .orElseThrow { BusinessException(CouponErrorCode.COUPON_NOT_FOUND) }
        couponRepository.delete(coupon)
    }

    @Transactional
    override fun toggleActive(couponUuid: String): CouponResponse {
        val coupon = couponRepository.findByCouponUuid(couponUuid)
            .orElseThrow { BusinessException(CouponErrorCode.COUPON_NOT_FOUND) }
        coupon.toggleActive()
        return CouponResponse.from(coupon)
    }
}
