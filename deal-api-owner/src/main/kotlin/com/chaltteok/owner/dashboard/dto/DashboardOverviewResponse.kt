package com.chaltteok.owner.dashboard.dto

import java.time.LocalDate

class DashboardOverviewResponse(
    val period: String,
    val from: LocalDate,
    val to: LocalDate,
    val totalRevenue: Long,
    val orderCount: Long,
    val avgOrderValue: Long,
    val newCustomers: Long,
    val repeatCustomers: Long,
    val cancelledCount: Long,
)
