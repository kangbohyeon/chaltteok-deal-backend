package com.chaltteok.owner.order.service

import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.owner.order.dto.OwnerOrderDetailResponse
import com.chaltteok.owner.order.dto.OwnerOrderListResponse
import java.time.LocalDate

interface OwnerOrderService {
    fun getOrderDetail(orderNumber: String): OwnerOrderDetailResponse
    fun getOrders(status: OrderStatus?, startDate: LocalDate?, endDate: LocalDate?, page: Int, size: Int): OwnerOrderListResponse
    fun cancelOrder(orderNumber: String)
}
