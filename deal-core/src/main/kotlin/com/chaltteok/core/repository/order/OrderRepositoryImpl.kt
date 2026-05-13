package com.chaltteok.core.repository.order

import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.QOrder
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.repository.order.dto.DailySalesAgg
import com.chaltteok.core.repository.order.dto.HourlySalesAgg
import com.chaltteok.core.repository.order.dto.SalesPeriodAgg
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
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

    override fun findByUserIdPaged(userId: Long, keyword: String?, pageable: Pageable): Page<Order> {
        var predicate = qOrder.user.id.eq(userId)
        if (!keyword.isNullOrBlank()) {
            predicate = predicate.and(qOrder.orderNumber.containsIgnoreCase(keyword))
        }

        val content = jpaQueryFactory
            .selectFrom(qOrder)
            .where(predicate)
            .orderBy(qOrder.orderedAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = jpaQueryFactory
            .select(qOrder.count())
            .from(qOrder)
            .where(predicate)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }
}
