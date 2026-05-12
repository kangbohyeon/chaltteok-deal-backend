package com.chaltteok.core.repository.order.dto

import java.time.LocalDate

class DailySalesAgg(
    val date: LocalDate,
    val orderCount: Long,
    val revenue: Long,
)
