package com.chaltteok.core.event

import com.chaltteok.core.domain.enums.PaymentMethod

data class OrderPlacedEvent(
    val userId: Long,
    val dailyStockId: Long,
    val paymentMethod: PaymentMethod,
    val quantity: Int = 1,
)
