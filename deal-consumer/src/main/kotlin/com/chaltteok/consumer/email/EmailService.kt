package com.chaltteok.consumer.email

import com.chaltteok.core.event.OrderCancelledEvent
import com.chaltteok.core.event.OrderCompletedEvent

interface EmailService {
    fun sendOrderConfirmation(event: OrderCompletedEvent, userEmail: String)
    fun sendOrderCancellation(event: OrderCancelledEvent, userEmail: String)
    fun sendPasswordReset(email: String, tempPassword: String)
}
