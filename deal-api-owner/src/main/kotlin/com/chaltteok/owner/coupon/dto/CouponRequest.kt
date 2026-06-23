package com.chaltteok.owner.coupon.dto

import com.chaltteok.core.domain.enums.DiscountType
import jakarta.validation.constraints.*
import java.time.LocalDate

data class CouponRequest(
    @field:NotBlank @field:Size(max = 50)
    val code: String,
    @field:NotBlank @field:Size(max = 100)
    val name: String,
    @field:NotNull
    val discountType: DiscountType,
    @field:Min(1)
    val discountValue: Int,
    @field:Min(0)
    val minOrderAmount: Int? = null,
    @field:Min(0)
    val maxDiscountAmount: Int? = null,
    @field:Min(1)
    val totalQuantity: Int? = null,
    @field:NotNull
    val startDate: LocalDate,
    @field:NotNull
    val endDate: LocalDate,
    val isActive: Boolean = true,
)
