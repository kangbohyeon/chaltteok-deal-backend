package com.chaltteok.owner.dashboard.dto

import java.time.LocalDate

class HourlySalesResponse(
    val date: LocalDate,
    val hourlySales: List<HourlySalesItem>,
)

class HourlySalesItem(
    val hour: Int,
    val orderCount: Long,
    val revenue: Long,
)
