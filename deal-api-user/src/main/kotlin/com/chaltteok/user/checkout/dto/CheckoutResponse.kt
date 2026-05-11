package com.chaltteok.user.checkout.dto

data class CheckoutResponse(
    val orderId: Long,
    val totalAmount: Long,
    val status: String,
)
