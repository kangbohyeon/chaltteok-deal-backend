package com.chaltteok.owner.order.service

import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.owner.order.dto.OwnerOrderDetailResponse
import com.chaltteok.owner.order.dto.OwnerOrderListResponse

interface OwnerOrderService {
    fun getOrderDetail(orderNumber: String): OwnerOrderDetailResponse
    fun getOrders(status: OrderStatus?, page: Int, size: Int): OwnerOrderListResponse
}
