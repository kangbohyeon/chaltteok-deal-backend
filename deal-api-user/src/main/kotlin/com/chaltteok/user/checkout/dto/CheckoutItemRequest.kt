package com.chaltteok.user.checkout.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class CheckoutItemRequest(
    @field:NotNull val productUuid: String,
    @field:Min(1) val quantity: Int,
    @field:NotNull val price: Long,
)
