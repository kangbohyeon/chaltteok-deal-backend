package com.chaltteok.user.order.dto

data class OrderHistoryResponse(
    val orderUuid: String,
    val totalPrice: Long,
    val status: String,
    val orderedAt: String,
    val items: List<OrderHistoryItemResponse>,
)
