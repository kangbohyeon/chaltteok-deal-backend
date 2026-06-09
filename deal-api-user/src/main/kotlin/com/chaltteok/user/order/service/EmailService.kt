package com.chaltteok.user.order.service

import com.chaltteok.core.event.OrderCompletedEvent

interface EmailService {
    fun sendOrderConfirmation(event: OrderCompletedEvent)
}
