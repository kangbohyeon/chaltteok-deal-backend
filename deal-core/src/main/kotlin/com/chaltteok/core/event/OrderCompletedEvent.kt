package com.chaltteok.core.event

data class OrderCompletedEvent(
    val orderId: Long,
    val orderNumber: String,
    val userEmail: String,
    val userName: String,
    val productName: String,
    val totalAmount: Long,
    val orderedAt: String,
)
