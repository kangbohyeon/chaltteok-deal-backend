package com.chaltteok.owner.order.service

import com.chaltteok.owner.order.dto.OwnerOrderDetailResponse

interface OwnerOrderService {
    fun getOrderDetail(orderNumber: String): OwnerOrderDetailResponse
}
