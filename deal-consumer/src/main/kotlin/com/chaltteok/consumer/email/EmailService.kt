package com.chaltteok.consumer.email

import com.chaltteok.core.event.OrderCompletedEvent

interface EmailService {
    fun sendOrderConfirmation(event: OrderCompletedEvent)
}
