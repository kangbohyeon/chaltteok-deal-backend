package com.chaltteok.user.order.dto

data class OrderResponse(
    val status: String,
    val message: String,
    val orderNumber: String? = null,
    val totalAmount: Long? = null,
) {
    companion object {
        fun pending() = OrderResponse(status = "PENDING", message = "주문이 접수되었습니다.")
        fun completed(orderNumber: String, totalAmount: Long) = OrderResponse(
            status = "COMPLETED",
            message = "주문이 완료되었습니다.",
            orderNumber = orderNumber,
            totalAmount = totalAmount,
        )
    }
}
