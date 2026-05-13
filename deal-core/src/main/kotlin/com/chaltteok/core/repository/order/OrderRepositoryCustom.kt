package com.chaltteok.core.repository.order

import com.chaltteok.core.domain.Order
import com.chaltteok.core.repository.order.dto.DailySalesAgg
import com.chaltteok.core.repository.order.dto.HourlySalesAgg
import com.chaltteok.core.repository.order.dto.SalesPeriodAgg
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.LocalDateTime

interface OrderRepositoryCustom {
    fun findSalesPeriodAgg(from: LocalDateTime, to: LocalDateTime): SalesPeriodAgg
    fun findDailySalesTrend(from: LocalDateTime, to: LocalDateTime): List<DailySalesAgg>
    fun findHourlySales(targetDate: LocalDate): List<HourlySalesAgg>
    fun findByUserIdPaged(userId: Long, keyword: String?, pageable: Pageable): Page<Order>
}