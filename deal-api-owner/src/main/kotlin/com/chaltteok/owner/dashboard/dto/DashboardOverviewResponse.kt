package com.chaltteok.owner.dashboard.dto

import java.time.LocalDateTime

class DashboardOverviewResponse(
    val period: String,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val totalRevenue: Long,
    val orderCount: Long,
    val avgOrderValue: Long,
    val newCustomers: Long,
    val repeatCustomers: Long,
    val cancelledCount: Long,
)
