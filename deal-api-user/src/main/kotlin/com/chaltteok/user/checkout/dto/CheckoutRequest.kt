package com.chaltteok.user.checkout.dto

import com.chaltteok.core.domain.enums.PaymentMethod
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

class CheckoutRequest(
    @field:NotEmpty @field:Valid val items: List<CheckoutItemRequest>,
    @field:NotNull @field:PositiveOrZero val totalAmount: Long,
    val paymentMethod: PaymentMethod,
)
