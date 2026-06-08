package com.chaltteok.user.order.service

import com.chaltteok.user.order.dto.AsyncOrderResponse
import com.chaltteok.user.order.dto.OrderRequest

interface OrderAsyncService {
    fun placeOrderAsync(userId: Long, request: OrderRequest): AsyncOrderResponse
}
