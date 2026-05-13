package com.chaltteok.user.order.dto

data class OrderHistoryItemResponse(
    val productName: String,
    val quantity: Int,
    val price: Long,
)
