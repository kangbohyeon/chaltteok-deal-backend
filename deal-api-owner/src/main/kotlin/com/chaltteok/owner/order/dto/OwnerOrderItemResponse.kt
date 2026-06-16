package com.chaltteok.owner.order.dto

import com.chaltteok.core.domain.OrderItem

class OwnerOrderItemResponse(
    val productName: String,
    val quantity: Int,
    val price: Int,
) {
    companion object {
        fun from(item: OrderItem) = OwnerOrderItemResponse(
            productName = item.product.name,
            quantity = item.quantity,
            price = item.price,
        )
    }
}
