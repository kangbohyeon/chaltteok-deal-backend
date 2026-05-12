package com.chaltteok.core.repository.order

import com.chaltteok.core.repository.order.dto.DailySalesAgg
import com.chaltteok.core.repository.order.dto.HourlySalesAgg
import com.chaltteok.core.repository.order.dto.SalesPeriodAgg
import java.time.LocalDate
import java.time.LocalDateTime

interface OrderRepositoryCustom {
    fun findSalesPeriodAgg(from: LocalDateTime, to: LocalDateTime): SalesPeriodAgg
    fun findDailySalesTrend(from: LocalDateTime, to: LocalDateTime): List<DailySalesAgg>
    fun findHourlySales(targetDate: LocalDate): List<HourlySalesAgg>
}