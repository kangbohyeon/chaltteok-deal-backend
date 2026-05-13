package com.chaltteok.owner.product.dto

import com.chaltteok.core.repository.product.dto.ProductWithOptionRow

class ProductListResponse(
    val id: Long,
    val uuid: String,
    val optionUuid: String,
    val name: String,
    val price: Int,
    val descp: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val isSoldOut: Boolean,
    val isRecommended: Boolean,
) {
    companion object {
        fun from(row: ProductWithOptionRow) = ProductListResponse(
            id = row.productId,
            uuid = row.productUuid,
            optionUuid = row.optionUuid,
            name = row.name,
            price = row.optionPrice,
            descp = row.description,
            imageUrl = row.imageUrl,
            isActive = row.isActive,
            isSoldOut = row.isSoldOut,
            isRecommended = row.isRecommended,
        )
    }
}
