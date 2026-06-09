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

    companion object {
        private const val MAX_PERIOD_DAYS = 365L
    }

    override fun getOverview(period: DashboardPeriod, from: LocalDate?, to: LocalDate?): DashboardOverviewResponse {
        val (fromDate, toDate) = if (from != null && to != null) {
            validateDateRange(from, to)
            Pair(from, to)
        } else {
            resolvePeriodRange(period)
        }

        val stats = orderStatsRepository.findAllByStatDateBetween(fromDate, toDate)
        val (orderCount, totalRevenue, cancelledCount) = stats.fold(Triple(0L, 0L, 0L)) { (oc, tr, cc), s ->
            Triple(oc + s.orderCount, tr + s.totalRevenue, cc + s.cancelledCount)
        }
        val avgOrderValue = if (orderCount > 0) totalRevenue / orderCount else 0L

        // tb_order_stats 미집계 항목 — user 테이블 직접 조회 불가피
        val fromDt = fromDate.atStartOfDay()
        val toDt = toDate.atTime(LocalTime.MAX)
        val newCustomers = userRepository.countNewUsers(fromDt, toDt)
        val repeatCustomers = userRepository.countRepeatOrderUsers(fromDt, toDt)

        return DashboardOverviewResponse(
            period = period.name,
            from = fromDate,
            to = toDate,
            totalRevenue = totalRevenue,
            orderCount = orderCount,
            avgOrderValue = avgOrderValue,
            newCustomers = newCustomers,
            repeatCustomers = repeatCustomers,
            cancelledCount = cancelledCount,
        )
    }

    override fun getSalesTrend(from: LocalDate, to: LocalDate): SalesTrendResponse {
        validateDateRange(from, to)
        val items = orderStatsRepository.findAllByStatDateBetween(from, to).map { stat ->
            SalesTrendItem(
                date = stat.statDate,
                orderCount = stat.orderCount,
                revenue = stat.totalRevenue,
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

    private fun validateDateRange(from: LocalDate, to: LocalDate) {
        require(!from.isAfter(to)) { "시작일은 종료일보다 이전이어야 합니다." }
        require(!to.isAfter(LocalDate.now())) { "종료일은 오늘 이전이어야 합니다." }
        require(!from.isBefore(to.minusDays(MAX_PERIOD_DAYS))) { "조회 기간은 최대 ${MAX_PERIOD_DAYS}일입니다." }
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
