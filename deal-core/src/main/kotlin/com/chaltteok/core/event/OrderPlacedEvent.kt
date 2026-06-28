package com.chaltteok.core.event

import com.chaltteok.core.domain.enums.PaymentMethod
import com.fasterxml.jackson.annotation.JsonProperty

data class OrderPlacedEvent(
    val userId: Long,
    val timeSaleStockId: Long,
    val paymentMethod: PaymentMethod,
    @JsonProperty(defaultValue = "1")
    val quantity: Int = 1,
)
