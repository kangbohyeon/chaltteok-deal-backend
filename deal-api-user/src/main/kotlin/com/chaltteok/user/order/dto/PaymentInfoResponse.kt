package com.chaltteok.user.order.dto

data class PaymentInfoResponse(
    val amount: Int,
    val pgProvider: String?,
    val paymentMethod: String?,
    val status: String,
    val paidAt: String?,
)
