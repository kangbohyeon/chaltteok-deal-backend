package com.chaltteok.user.wish.service

import com.chaltteok.user.wish.dto.WishListResponse

interface WishService {
    fun getWishes(userId: Long): WishListResponse
    fun addWish(userId: Long, productUuid: String)
    fun removeWish(userId: Long, productUuid: String)
}
