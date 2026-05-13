package com.chaltteok.user.order.dto

data class OrderHistoryResponse(
    val orderNumber: String,
    val totalPrice: Long,
    val status: String,
    val orderedAt: String,
    val items: List<OrderHistoryItemResponse>,
    val payment: PaymentInfoResponse?,
    val canCancel: Boolean,
)
