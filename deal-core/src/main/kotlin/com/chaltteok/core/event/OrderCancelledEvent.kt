package com.chaltteok.core.event

data class OrderCancelledEvent(
    val orderId: Long,
    val orderNumber: String,
    val userName: String,
    val totalAmount: Long,
    val cancelledAt: String,
)
