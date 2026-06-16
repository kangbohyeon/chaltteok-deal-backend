package com.chaltteok.owner.order.dto

import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.Payment
import java.time.LocalDateTime

class OwnerOrderDetailResponse(
    val orderNumber: String,
    val totalPrice: Int,
    val status: String,
    val orderedAt: LocalDateTime,
    val items: List<OwnerOrderItemResponse>,
    val payment: OwnerOrderPaymentResponse?,
    val canCancel: Boolean,
) {
    companion object {
        fun from(order: Order, items: List<OrderItem>, payment: Payment?) = OwnerOrderDetailResponse(
            orderNumber = order.orderNumber,
            totalPrice = order.totalPrice,
            status = order.status.name,
            orderedAt = order.orderedAt,
            items = items.map { OwnerOrderItemResponse.from(it) },
            payment = payment?.let { OwnerOrderPaymentResponse.from(it) },
            canCancel = order.isCancellable(),
        )
    }
}
