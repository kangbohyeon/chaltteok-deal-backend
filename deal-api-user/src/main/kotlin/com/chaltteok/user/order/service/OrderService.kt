package com.chaltteok.user.order.service

import com.chaltteok.user.order.dto.OrderResponse
import com.chaltteok.user.order.dto.OrderHistoryPageResponse
import com.chaltteok.user.order.dto.OrderHistoryResponse
import com.chaltteok.user.order.dto.OrderRequest
import org.springframework.data.domain.Pageable

interface OrderService {
    fun placeOrder(userId: Long, request: OrderRequest): OrderResponse
    fun cancelOrder(userId: Long, orderNumber: String)
    fun getOrderHistory(
        userId: Long,
        keyword: String?,
        status: String?,
        fromDate: String?,
        toDate: String?,
        paymentStatus: String?,
        pageable: Pageable,
    ): OrderHistoryPageResponse
    fun getOrderDetail(userId: Long, orderNumber: String): OrderHistoryResponse
}
