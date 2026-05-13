package com.chaltteok.owner.product.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

class ProductUpdateRequest(
    @field:NotNull(message = "name must not be null")
    val name: String,

    @field:NotNull(message = "price must not be null")
    @field:Min(value = 100, message = "price must be more than 100")
    val price: Int,

    val descp: String?,
    val isActive: Boolean = true,
    val isSoldOut: Boolean = false,
    val isRecommended: Boolean = false,
)
