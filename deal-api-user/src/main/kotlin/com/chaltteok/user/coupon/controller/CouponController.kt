package com.chaltteok.user.coupon.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.coupon.dto.CouponValidateResponse
import com.chaltteok.user.coupon.service.CouponService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/coupons")
class CouponController(
    private val couponService: CouponService,
) {
    @GetMapping("/validate")
    fun validate(
        @RequestParam code: String,
        @RequestParam amount: Int,
    ): ResponseEntity<ResponseDTO<CouponValidateResponse>> =
        ResponseEntity.ok(ResponseDTO.success(couponService.validate(code, amount)))
}
