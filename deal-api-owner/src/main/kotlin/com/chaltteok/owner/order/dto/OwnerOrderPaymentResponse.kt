package com.chaltteok.owner.order.dto

import com.chaltteok.core.domain.Payment
import java.time.LocalDateTime

class OwnerOrderPaymentResponse(
    val amount: Int,
    val paymentMethod: String?,
    val status: String,
    val paidAt: LocalDateTime?,
) {
    companion object {
        fun from(payment: Payment) = OwnerOrderPaymentResponse(
            amount = payment.amount,
            paymentMethod = payment.paymentMethod,
            status = payment.status.name,
            paidAt = payment.paidAt,
        )
    }
}
