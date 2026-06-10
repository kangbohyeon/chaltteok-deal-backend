package com.chaltteok.core.event

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class OrderCancelledEvent(
    val orderId: Long,
    val orderNumber: String,
    val userName: String,
    val totalAmount: Long,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val cancelledAt: LocalDateTime,
) : DomainEvent
