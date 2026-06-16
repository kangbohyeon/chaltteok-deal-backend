package com.chaltteok.owner.order.dto

import java.time.LocalDateTime

class OwnerOrderSummaryResponse(
    val orderNumber: String,
    val productName: String,
    val totalPrice: Int,
    val status: String,
    val orderedAt: LocalDateTime,
    val itemCount: Int,
)
