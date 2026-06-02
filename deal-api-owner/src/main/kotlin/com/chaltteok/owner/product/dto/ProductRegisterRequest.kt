package com.chaltteok.owner.product.dto

import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.ProductOption
import com.chaltteok.owner.product.enums.StockType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

class ProductRegisterRequest(
    @field:NotNull(message = "name must be not null or empty")
    val name: String,

    @field:NotNull(message = "price must be not null or empty")
    @field:Min(value = 100, message = "price must be more than 100")
    val price: Int,

    val descp: String?,
    val isActive: Boolean = true,
    val isSoldOut: Boolean = false,
    val isRecommended: Boolean = false,
    @field:Min(value = 0, message = "stock quantity must be 0 or more")
    val stockQuantity: Int? = null,
    val displayOrder: Int = 0,
) {
    fun toProduct(imageUrl: String?): Product {
        val soldOut = isSoldOut || stockQuantity == 0
        return Product(
            name = name,
            description = descp,
            imageUrl = imageUrl,
            price = price,
            isActive = isActive,
            isSoldOut = soldOut,
            isRecommended = isRecommended,
            stockQuantity = stockQuantity,
            currentStock = stockQuantity,
            displayOrder = displayOrder,
        )
    }

    fun toProductOption(product: Product): ProductOption {
        return ProductOption(
            optionName = StockType.NORMAL.name,
            product = product,
            price = price,
        )
    }
}