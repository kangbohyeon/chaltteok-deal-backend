package com.chaltteok.consumer.email

import com.chaltteok.core.event.OrderCancelledEvent
import com.chaltteok.core.event.OrderCompletedEvent

interface EmailService {
    fun sendOrderConfirmation(event: OrderCompletedEvent)
    fun sendOrderCancellation(event: OrderCancelledEvent)
}
