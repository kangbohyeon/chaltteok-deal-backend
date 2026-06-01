package com.chaltteok.core.repository.product.dto

class ProductWithOptionRow(
    val productId: Long,
    val productUuid: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val price: Int,
    val isActive: Boolean,
    val isSoldOut: Boolean,
    val isRecommended: Boolean,
    val stockQuantity: Int?,
    val currentStock: Int?,
    val optionUuid: String,
    val optionPrice: Int,
)
