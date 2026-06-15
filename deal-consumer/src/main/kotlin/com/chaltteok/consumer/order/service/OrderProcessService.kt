package com.chaltteok.consumer.order.service

import com.chaltteok.core.domain.enums.PaymentMethod

interface OrderProcessService {
    fun processOrder(userId: Long, dailyStockId: Long, quantity: Int, paymentMethod: PaymentMethod)
}
