package com.chaltteok.consumer.order.service

interface OrderProcessService {
    fun processOrder(userId: Long, dailyStockId: Long)
}
