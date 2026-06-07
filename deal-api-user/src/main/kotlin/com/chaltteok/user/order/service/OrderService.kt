package com.chaltteok.user.order.service

import com.chaltteok.user.checkout.dto.CheckoutResponse
import com.chaltteok.user.order.dto.OrderRequest

interface OrderService {
    fun placeOrder(userId: Long, request: OrderRequest): CheckoutResponse
    fun cancelOrder(userId: Long, orderNumber: String)
}
