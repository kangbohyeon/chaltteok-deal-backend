package com.chaltteok.core.repository.orderitem

import com.chaltteok.core.domain.QOrder
import com.chaltteok.core.domain.QOrderItem
import com.chaltteok.core.domain.QProduct
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.repository.orderitem.dto.TopProductAgg
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class OrderItemRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : OrderItemRepositoryCustom {

    private val qOrderItem = QOrderItem.orderItem
    private val qOrder = QOrder.order
    private val qProduct = QProduct.product

    override fun findTopProducts(from: LocalDateTime, to: LocalDateTime, limit: Int): List<TopProductAgg> {
        val uuidExpr = qProduct.productUuid
        val nameExpr = qProduct.name
        val qtyExpr = qOrderItem.quantity.longValue().sum()
        val revenueExpr = qOrderItem.price.multiply(qOrderItem.quantity).longValue().sum()

        return jpaQueryFactory
            .select(uuidExpr, nameExpr, qtyExpr, revenueExpr)
            .from(qOrderItem)
            .join(qOrderItem.order, qOrder)
            .join(qOrderItem.product, qProduct)
            .where(
                qOrder.status.eq(OrderStatus.COMPLETED),
                qOrder.orderedAt.between(from, to),
            )
            .groupBy(qProduct.id, qProduct.productUuid, qProduct.name)
            .orderBy(qtyExpr.desc())
            .limit(limit.toLong())
            .fetch()
            .map { tuple ->
                TopProductAgg(
                    productUuid = tuple.get(uuidExpr)!!,
                    productName = tuple.get(nameExpr)!!,
                    totalQty = tuple.get(qtyExpr) ?: 0L,
                    totalRevenue = tuple.get(revenueExpr) ?: 0L,
                )
            }
    }
}
