package com.chaltteok.owner.product.dto

import com.chaltteok.core.repository.product.dto.ProductWithOptionRow

class ProductDetailResponse(
    val uuid: String,
    val optionUuid: String,
    val name: String,
    val price: Int,
    val optionPrice: Int,
    val description: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val isSoldOut: Boolean,
    val isRecommended: Boolean,
    val stockQuantity: Int?,
    val currentStock: Int?,
    val displayOrder: Int,
) {
    companion object {
        fun from(row: ProductWithOptionRow) = ProductDetailResponse(
            uuid = row.productUuid,
            optionUuid = row.optionUuid,
            name = row.name,
            price = row.price,
            optionPrice = row.optionPrice,
            description = row.description,
            imageUrl = row.imageUrl,
            isActive = row.isActive,
            isSoldOut = row.isSoldOut,
            isRecommended = row.isRecommended,
            stockQuantity = row.stockQuantity,
            currentStock = row.currentStock,
            displayOrder = row.displayOrder,
        )
    }
}
