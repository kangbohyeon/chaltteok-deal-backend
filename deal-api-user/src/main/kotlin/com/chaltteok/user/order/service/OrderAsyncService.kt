package com.chaltteok.user.order.service

import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.dto.OrderResponse

interface OrderAsyncService {
    fun placeOrderAsync(userId: Long, request: OrderRequest): OrderResponse
}
