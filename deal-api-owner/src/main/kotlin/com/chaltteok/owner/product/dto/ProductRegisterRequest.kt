package com.chaltteok.owner.product.dto

import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.ProductOption
import com.chaltteok.owner.product.enums.StockType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class ProductRegisterRequest(
    @field:NotNull(message = "name must be not null or empty")
    val name: String,

    @field:NotNull(message = "price must be not null or empty")
    @field:Min(value = 100, message = "price must be more than 100")
    val price: Int,

    val descp: String?
) {
    fun toProduct(imageUrl : String?): Product {
        return Product(
            name = name,
            description = descp,
            imageUrl = imageUrl,
            isActive = true
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