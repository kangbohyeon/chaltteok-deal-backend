package com.chaltteok.user.checkout.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class CheckoutRequest(
    @field:NotEmpty @field:Valid val items: List<CheckoutItemRequest>,
    @field:NotNull @field:PositiveOrZero val totalAmount: Long,
    @field:NotNull val paymentMethod: String,
)
