package com.chaltteok.user.order.dto

data class AsyncOrderResponse(
    val status: String,
    val message: String,
) {
    companion object {
        fun pending() = AsyncOrderResponse(status = "PENDING", message = "주문이 접수되었습니다.")
    }
}
