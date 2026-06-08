package com.chaltteok.owner.product.dto

import com.chaltteok.core.domain.ProductConstants
import jakarta.validation.constraints.Max
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
    @field:Min(value = 0, message = "stock quantity must be 0 or more")
    val stockQuantity: Int? = null,
    @field:Min(value = 0, message = "current stock must be 0 or more")
    val currentStock: Int? = null,
    @field:Min(value = 0, message = "display order must be 0 or more")
    @field:Max(value = ProductConstants.DISPLAY_ORDER_MAX, message = "display order must be ${ProductConstants.DISPLAY_ORDER_MAX} or less")
    val displayOrder: Int? = null,
    val deleteImage: Boolean = false,
)
