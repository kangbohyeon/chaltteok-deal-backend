package com.chaltteok.owner.dashboard.service

import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.owner.dashboard.dto.DashboardOverviewResponse
import com.chaltteok.owner.dashboard.dto.HourlySalesItem
import com.chaltteok.owner.dashboard.dto.HourlySalesResponse
import com.chaltteok.owner.dashboard.dto.SalesTrendItem
import com.chaltteok.owner.dashboard.dto.SalesTrendResponse
import com.chaltteok.owner.dashboard.dto.TopProductItem
import com.chaltteok.owner.dashboard.dto.TopProductsResponse
import com.chaltteok.owner.dashboard.enums.DashboardPeriod
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Service
class DashboardServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val userRepository: UserRepository,
) : DashboardService {

    override fun getOverview(period: DashboardPeriod, from: LocalDate?, to: LocalDate?): DashboardOverviewResponse {
        if (from != null && to != null) {
            require(!from.isAfter(to)) { "시작일은 종료일보다 이전이어야 합니다." }
            require(!to.isAfter(LocalDate.now())) { "종료일은 오늘 이전이어야 합니다." }
            require(!from.isBefore(to.minusDays(365))) { "조회 기간은 최대 365일입니다." }
        }

        val (fromDt, toDt) = if (from != null && to != null) {
            Pair(from.atStartOfDay(), to.atTime(LocalTime.MAX))
        } else {
            resolvePeriodRange(period)
        }

        val salesAgg = orderRepository.findSalesPeriodAgg(fromDt, toDt)
        val newCustomers = userRepository.countNewUsers(fromDt, toDt)
        val repeatCustomers = userRepository.countRepeatOrderUsers(fromDt, toDt)
        val avgOrderValue = if (salesAgg.orderCount > 0) salesAgg.totalRevenue / salesAgg.orderCount else 0L

        return DashboardOverviewResponse(
            period = period.name,
            from = fromDt,
            to = toDt,
            totalRevenue = salesAgg.totalRevenue,
            orderCount = salesAgg.orderCount,
            avgOrderValue = avgOrderValue,
            newCustomers = newCustomers,
            repeatCustomers = repeatCustomers,
            cancelledCount = salesAgg.cancelledCount,
        )
    }

    override fun getSalesTrend(from: LocalDate, to: LocalDate): SalesTrendResponse {
        val fromDt = from.atStartOfDay()
        val toDt = to.atTime(LocalTime.MAX)

        val items = orderRepository.findDailySalesTrend(fromDt, toDt).map { agg ->
            SalesTrendItem(date = agg.date, orderCount = agg.orderCount, revenue = agg.revenue)
        }
        return SalesTrendResponse(trend = items)
    }

    override fun getTopProducts(from: LocalDate, to: LocalDate, limit: Int): TopProductsResponse {
        val fromDt = from.atStartOfDay()
        val toDt = to.atTime(LocalTime.MAX)

        val products = orderItemRepository.findTopProducts(fromDt, toDt, limit).map { agg ->
            TopProductItem(
                productUuid = agg.productUuid,
                productName = agg.productName,
                totalQty = agg.totalQty,
                totalRevenue = agg.totalRevenue,
            )
        }
        return TopProductsResponse(products = products)
    }

    override fun getHourlySales(date: LocalDate): HourlySalesResponse {
        val items = orderRepository.findHourlySales(date).map { agg ->
            HourlySalesItem(hour = agg.hour, orderCount = agg.orderCount, revenue = agg.revenue)
        }
        return HourlySalesResponse(date = date, hourlySales = items)
    }

    private fun resolvePeriodRange(period: DashboardPeriod): Pair<LocalDateTime, LocalDateTime> {
        val today = LocalDate.now()
        return when (period) {
            DashboardPeriod.DAILY -> Pair(today.atStartOfDay(), today.atTime(LocalTime.MAX))
            DashboardPeriod.WEEKLY -> Pair(today.minusDays(6).atStartOfDay(), today.atTime(LocalTime.MAX))
            DashboardPeriod.MONTHLY -> Pair(
                today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(),
                today.atTime(LocalTime.MAX),
            )
        }
    }
}
