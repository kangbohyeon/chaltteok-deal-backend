package com.chaltteok.user.wish.dto

import com.chaltteok.core.domain.Wish
import java.time.LocalDateTime

data class WishResponse(
    val wishUuid: String,
    val productUuid: String,
    val productName: String,
    val productPrice: Int,
    val imageUrl: String?,
    val wishedAt: LocalDateTime,
) {
    companion object {
        fun from(wish: Wish) = WishResponse(
            wishUuid = wish.wishUuid,
            productUuid = wish.product.productUuid,
            productName = wish.product.name,
            productPrice = wish.product.price,
            imageUrl = wish.product.imageUrl,
            wishedAt = wish.createdAt,
        )
    }
}

data class WishListResponse(
    val wishes: List<WishResponse>,
    val totalCount: Int,
)
