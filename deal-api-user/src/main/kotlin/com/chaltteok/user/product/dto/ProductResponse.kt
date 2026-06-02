package com.chaltteok.user.product.dto

import com.chaltteok.core.domain.Product

data class ProductResponse(
    val id: Long,
    val productUuid: String,
    val name: String,
    val price: Long,
    val description: String?,
    val thumbnailUrl: String?,
    val soldOut: Boolean,
    val recommended: Boolean = false,
    val commentCount: Int = 0,
    val averageRating: Double? = null,
    val salesCount: Long = 0,
) {
    companion object {
        fun from(product: Product, commentCount: Int = 0, averageRating: Double? = null, salesCount: Long = 0) = ProductResponse(
            id = product.id!!,
            productUuid = product.productUuid,
            name = product.name,
            price = product.price.toLong(),
            description = product.description?.ifBlank { null },
            thumbnailUrl = product.imageUrl,
            soldOut = product.isSoldOut,
            recommended = product.isRecommended,
            commentCount = commentCount,
            averageRating = averageRating,
            salesCount = salesCount,
        )
    }
}
