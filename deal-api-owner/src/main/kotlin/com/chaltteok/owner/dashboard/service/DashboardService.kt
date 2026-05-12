package com.chaltteok.owner.dashboard.service

import com.chaltteok.owner.dashboard.dto.DashboardOverviewResponse
import com.chaltteok.owner.dashboard.dto.HourlySalesResponse
import com.chaltteok.owner.dashboard.dto.SalesTrendResponse
import com.chaltteok.owner.dashboard.dto.TopProductsResponse
import com.chaltteok.owner.dashboard.enums.DashboardPeriod
import java.time.LocalDate

interface DashboardService {
    fun getOverview(period: DashboardPeriod): DashboardOverviewResponse
    fun getSalesTrend(from: LocalDate, to: LocalDate): SalesTrendResponse
    fun getTopProducts(from: LocalDate, to: LocalDate, limit: Int): TopProductsResponse
    fun getHourlySales(date: LocalDate): HourlySalesResponse
}
