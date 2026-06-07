package com.chaltteok.user.order.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class OrderRequest(
    @field:NotBlank
    val stockUuid: String,
    @field:Min(1)
    val quantity: Int = 1,
)
