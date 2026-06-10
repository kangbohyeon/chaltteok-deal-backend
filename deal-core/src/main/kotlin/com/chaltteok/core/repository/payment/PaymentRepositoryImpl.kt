package com.chaltteok.core.repository.payment

import com.chaltteok.core.domain.Payment
import com.chaltteok.core.domain.QPayment
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : PaymentRepositoryCustom {

    private val qPayment = QPayment.payment

    override fun findByOrderIds(orderIds: List<Long>): List<Payment> {
        if (orderIds.isEmpty()) return emptyList()
        return jpaQueryFactory
            .selectFrom(qPayment)
            .where(qPayment.order.id.`in`(orderIds))
            .fetch()
    }

    override fun findByOrderId(orderId: Long): Payment? =
        jpaQueryFactory.selectFrom(qPayment).where(qPayment.order.id.eq(orderId)).fetchOne()
}