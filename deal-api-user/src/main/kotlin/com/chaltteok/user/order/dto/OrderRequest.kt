package com.chaltteok.user.order.dto

import com.chaltteok.core.domain.enums.PaymentMethod
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class OrderRequest(
    @field:NotBlank
    val stockUuid: String,
    @field:Min(value = 1, message = "수량은 1 이상이어야 합니다")
    @field:Max(value = 100, message = "1회 최대 구매 수량은 100개입니다")
    val quantity: Int = 1,
    val paymentMethod: PaymentMethod,
)
