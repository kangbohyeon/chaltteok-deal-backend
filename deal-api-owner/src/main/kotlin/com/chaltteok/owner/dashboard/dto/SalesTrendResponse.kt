package com.chaltteok.owner.dashboard.dto

import java.time.LocalDate

class SalesTrendResponse(
    val trend: List<SalesTrendItem>,
)

class SalesTrendItem(
    val date: LocalDate,
    val orderCount: Long,
    val revenue: Long,
)
