package com.chaltteok.owner.coupon.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.coupon.dto.CouponRequest
import com.chaltteok.owner.coupon.dto.CouponResponse
import com.chaltteok.owner.coupon.service.OwnerCouponService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/owner/coupons")
class OwnerCouponController(
    private val couponService: OwnerCouponService,
) {
    @GetMapping
    fun getCoupons(): ResponseEntity<ResponseDTO<List<CouponResponse>>> =
        ResponseEntity.ok(ResponseDTO.success(couponService.getCoupons()))

    @PostMapping
    fun createCoupon(@Valid @RequestBody request: CouponRequest): ResponseEntity<ResponseDTO<CouponResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success(couponService.createCoupon(request)))

    @PutMapping("/{couponUuid}")
    fun updateCoupon(
        @PathVariable couponUuid: String,
        @Valid @RequestBody request: CouponRequest,
    ): ResponseEntity<ResponseDTO<CouponResponse>> =
        ResponseEntity.ok(ResponseDTO.success(couponService.updateCoupon(couponUuid, request)))

    @DeleteMapping("/{couponUuid}")
    fun deleteCoupon(@PathVariable couponUuid: String): ResponseEntity<Void> {
        couponService.deleteCoupon(couponUuid)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{couponUuid}/toggle")
    fun toggleActive(@PathVariable couponUuid: String): ResponseEntity<ResponseDTO<CouponResponse>> =
        ResponseEntity.ok(ResponseDTO.success(couponService.toggleActive(couponUuid)))
}
