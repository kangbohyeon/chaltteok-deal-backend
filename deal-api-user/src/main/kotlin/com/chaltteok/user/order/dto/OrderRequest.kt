package com.chaltteok.user.order.dto

import jakarta.validation.constraints.NotNull

data class OrderRequest(
    @field:NotNull
    val dailyStockId: Long,
)
