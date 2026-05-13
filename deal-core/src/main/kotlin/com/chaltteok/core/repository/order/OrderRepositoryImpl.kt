package com.chaltteok.core.repository.order

import com.chaltteok.core.domain.QOrder
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.repository.order.dto.DailySalesAgg
import com.chaltteok.core.repository.order.dto.HourlySalesAgg
import com.chaltteok.core.repository.order.dto.SalesPeriodAgg
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class OrderRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : OrderRepositoryCustom {

    private val qOrder = QOrder.order

    override fun findSalesPeriodAgg(from: LocalDateTime, to: LocalDateTime): SalesPeriodAgg {
        val completedCount = jpaQueryFactory
            .select(qOrder.id.count())
            .from(qOrder)
            .where(
                qOrder.status.eq(OrderStatus.COMPLETED),
                qOrder.orderedAt.between(from, to),
            )
            .fetchOne() ?: 0L

        val totalRevenue = jpaQueryFactory
            .select(qOrder.totalPrice.longValue().sum())
            .from(qOrder)
            .where(
                qOrder.status.eq(OrderStatus.COMPLETED),
                qOrder.orderedAt.between(from, to),
            )
            .fetchOne() ?: 0L

        val cancelledCount = jpaQueryFactory
            .select(qOrder.id.count())
            .from(qOrder)
            .where(
                qOrder.status.`in`(OrderStatus.CANCELLED, OrderStatus.FAILED),
                qOrder.orderedAt.between(from, to),
            )
            .fetchOne() ?: 0L

        return SalesPeriodAgg(
            orderCount = completedCount,
            totalRevenue = totalRevenue,
            cancelledCount = cancelledCount,
        )
    }

    override fun findDailySalesTrend(from: LocalDateTime, to: LocalDateTime): List<DailySalesAgg> {
        val dateExpr = Expressions.dateTemplate(java.sql.Date::class.java, "DATE({0})", qOrder.orderedAt)
        val countExpr = qOrder.id.count()
        val revenueExpr = qOrder.totalPrice.longValue().sum()

        return jpaQueryFactory
            .select(dateExpr, countExpr, revenueExpr)
            .from(qOrder)
            .where(
                qOrder.status.eq(OrderStatus.COMPLETED),
                qOrder.orderedAt.between(from, to),
            )
            .groupBy(dateExpr)
            .orderBy(dateExpr.asc())
            .fetch()
            .map { tuple ->
                DailySalesAgg(
                    date = tuple.get(dateExpr)!!.toLocalDate(),
                    orderCount = tuple.get(countExpr) ?: 0L,
                    revenue = tuple.get(revenueExpr) ?: 0L,
                )
            }
    }

    override fun findHourlySales(targetDate: LocalDate): List<HourlySalesAgg> {
        val from = targetDate.atStartOfDay()
        val to = targetDate.atTime(23, 59, 59)
        val hourExpr = Expressions.numberTemplate(Int::class.java, "HOUR({0})", qOrder.orderedAt)
        val countExpr = qOrder.id.count()
        val revenueExpr = qOrder.totalPrice.longValue().sum()

        return jpaQueryFactory
            .select(hourExpr, countExpr, revenueExpr)
            .from(qOrder)
            .where(
                qOrder.status.eq(OrderStatus.COMPLETED),
                qOrder.orderedAt.between(from, to),
            )
            .groupBy(hourExpr)
            .orderBy(hourExpr.asc())
            .fetch()
            .map { tuple ->
                HourlySalesAgg(
                    hour = tuple.get(hourExpr) ?: 0,
                    orderCount = tuple.get(countExpr) ?: 0L,
                    revenue = tuple.get(revenueExpr) ?: 0L,
                )
            }
    }
}
