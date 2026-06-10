package com.chaltteok.core.event

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class OrderCompletedEvent(
    val orderId: Long,
    val orderNumber: String,
    val userName: String,
    val productName: String,
    val totalAmount: Long,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val orderedAt: LocalDateTime,
)
