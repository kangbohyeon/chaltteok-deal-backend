package com.chaltteok.user.order.service

import com.chaltteok.user.order.dto.OrderHistoryResponse

interface OrderQueryService {
    fun getOrderHistory(userId: Long): List<OrderHistoryResponse>
}
