package com.chaltteok.user.order.dto

data class OrderResponse(
    val status: String,
    val message: String,
) {
    companion object {
        fun pending() = OrderResponse(status = "PENDING", message = "주문이 접수되었습니다.")
    }
}
