package com.chaltteok.user.order.service

import com.chaltteok.user.order.dto.OrderRequest

interface OrderService {
    fun placeOrder(userId: Long, request: OrderRequest)
    fun cancelOrder(userId: Long, orderNumber: String)
}
