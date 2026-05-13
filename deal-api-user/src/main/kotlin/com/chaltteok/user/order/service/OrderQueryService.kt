package com.chaltteok.user.order.service

import com.chaltteok.user.order.dto.OrderHistoryPageResponse
import com.chaltteok.user.order.dto.OrderHistoryResponse
import org.springframework.data.domain.Pageable

interface OrderQueryService {
    fun getOrderHistory(userId: Long, keyword: String?, pageable: Pageable): OrderHistoryPageResponse
    fun getOrderDetail(userId: Long, orderNumber: String): OrderHistoryResponse
}
