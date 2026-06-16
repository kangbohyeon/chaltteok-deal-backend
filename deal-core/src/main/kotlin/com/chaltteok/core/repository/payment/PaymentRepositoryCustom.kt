package com.chaltteok.core.repository.payment

import com.chaltteok.core.domain.Payment

interface PaymentRepositoryCustom {
    fun findByOrderIds(orderIds: List<Long>): List<Payment>
    fun findByOrderId(orderId: Long): Payment?
}