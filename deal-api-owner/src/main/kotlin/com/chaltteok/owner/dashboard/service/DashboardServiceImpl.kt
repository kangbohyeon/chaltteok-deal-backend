package com.chaltteok.owner.dashboard.service

import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.orderstats.OrderStatsRepository
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
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Service
class DashboardServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val orderStatsRepository: OrderStatsRepository,
    private val userRepository: UserRepository,
) : DashboardService {

    override fun getOverview(period: DashboardPeriod, from: LocalDate?, to: LocalDate?): DashboardOverviewResponse {
        if (from != null && to != null) {
            require(!from.isAfter(to)) { "시작일은 종료일보다 이전이어야 합니다." }
            require(!to.isAfter(LocalDate.now())) { "종료일은 오늘 이전이어야 합니다." }
            require(!from.isBefore(to.minusDays(365))) { "조회 기간은 최대 365일입니다." }
        }

        val (fromDate, toDate) = if (from != null && to != null) {
            Pair(from, to)
        } else {
            resolvePeriodRange(period)
        }

        // tb_order_stats 사전 집계 조회 (날짜별 최대 365건)
        val stats = orderStatsRepository.findAllByStatDateBetween(fromDate, toDate)
        val orderCount = stats.sumOf { it.orderCount }
        val totalRevenue = stats.sumOf { it.totalRevenue }
        val cancelledCount = stats.sumOf { it.cancelledCount }
        val avgOrderValue = if (orderCount > 0) totalRevenue / orderCount else 0L

        // 신규·재구매 고객은 user 차원 집계 → userRepository 유지
        val fromDt = fromDate.atStartOfDay()
        val toDt = toDate.atTime(LocalTime.MAX)
        val newCustomers = userRepository.countNewUsers(fromDt, toDt)
        val repeatCustomers = userRepository.countRepeatOrderUsers(fromDt, toDt)

        return DashboardOverviewResponse(
            period = period.name,
            from = fromDt,
            to = toDt,
            totalRevenue = totalRevenue,
            orderCount = orderCount,
            avgOrderValue = avgOrderValue,
            newCustomers = newCustomers,
            repeatCustomers = repeatCustomers,
            cancelledCount = cancelledCount,
        )
    }

    override fun getSalesTrend(from: LocalDate, to: LocalDate): SalesTrendResponse {
        // tb_order_stats 사전 집계 조회 → findDailySalesTrend GROUP BY 쿼리 대체
        val items = orderStatsRepository.findAllByStatDateBetween(from, to).map { stats ->
            SalesTrendItem(
                date = stats.statDate,
                orderCount = stats.orderCount,
                revenue = stats.totalRevenue,
            )
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

    private fun resolvePeriodRange(period: DashboardPeriod): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (period) {
            DashboardPeriod.DAILY -> Pair(today, today)
            DashboardPeriod.WEEKLY -> Pair(today.minusDays(6), today)
            DashboardPeriod.MONTHLY -> Pair(today.with(TemporalAdjusters.firstDayOfMonth()), today)
        }
    }
}
