package com.chaltteok.consumer.order.service

import com.chaltteok.core.domain.enums.PaymentMethod

data class OrderProcessCommand(
    val userId: Long,
    val timeSaleStockId: Long,
    val quantity: Int,
    val paymentMethod: PaymentMethod,
)
