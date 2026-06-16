package com.chaltteok.core.repository.order

import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.QOrder
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.repository.order.dto.DailySalesAgg
import com.chaltteok.core.repository.order.dto.HourlySalesAgg
import com.chaltteok.core.repository.order.dto.SalesPeriodAgg
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
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
        val completedCountExpr: NumberExpression<Long> = Expressions.numberTemplate(
            Long::class.java,
            "SUM(CASE WHEN {0} = 'COMPLETED' THEN 1 ELSE 0 END)",
            qOrder.status,
        )
        val revenueExpr: NumberExpression<Long> = Expressions.numberTemplate(
            Long::class.java,
            "SUM(CASE WHEN {0} = 'COMPLETED' THEN {1} ELSE 0 END)",
            qOrder.status,
            qOrder.totalPrice,
        )
        val cancelledCountExpr: NumberExpression<Long> = Expressions.numberTemplate(
            Long::class.java,
            "SUM(CASE WHEN {0} IN ('CANCELLED', 'FAILED') THEN 1 ELSE 0 END)",
            qOrder.status,
        )

        val row = jpaQueryFactory
            .select(completedCountExpr, revenueExpr, cancelledCountExpr)
            .from(qOrder)
            .where(qOrder.orderedAt.between(from, to))
            .fetchOne()

        return SalesPeriodAgg(
            orderCount = row?.get(completedCountExpr) ?: 0L,
            totalRevenue = row?.get(revenueExpr) ?: 0L,
            cancelledCount = row?.get(cancelledCountExpr) ?: 0L,
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

    override fun findByUserIdPaged(
        userId: Long,
        keyword: String?,
        status: OrderStatus?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        paymentStatus: String?,
        pageable: Pageable,
    ): Page<Order> {
        val qPayment = com.chaltteok.core.domain.QPayment.payment

        var predicate = qOrder.user.id.eq(userId)
        if (!keyword.isNullOrBlank()) {
            predicate = predicate.and(qOrder.orderNumber.containsIgnoreCase(keyword))
        }
        if (status != null) {
            predicate = predicate.and(qOrder.status.eq(status))
        }
        if (fromDate != null) {
            predicate = predicate.and(qOrder.orderedAt.goe(fromDate.atStartOfDay()))
        }
        if (toDate != null) {
            predicate = predicate.and(qOrder.orderedAt.loe(toDate.atTime(23, 59, 59)))
        }
        if (!paymentStatus.isNullOrBlank()) {
            val matchingOrderIds = jpaQueryFactory
                .select(qPayment.order.id)
                .from(qPayment)
                .join(qPayment.order, qOrder)
                .where(
                    qPayment.order.user.id.eq(userId),
                    qPayment.status.stringValue().eq(paymentStatus),
                )
                .fetch()
            predicate = predicate.and(qOrder.id.`in`(matchingOrderIds))
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
