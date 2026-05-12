package com.chaltteok.core.repository.user

import com.chaltteok.core.domain.QOrder
import com.chaltteok.core.domain.QUser
import com.chaltteok.core.domain.enums.OrderStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UserRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserRepositoryCustom {

    private val qUser = QUser.user
    private val qOrder = QOrder.order

    override fun countNewUsers(from: LocalDateTime, to: LocalDateTime): Long {
        return queryFactory
            .select(qUser.id.count())
            .from(qUser)
            .where(qUser.createdAt.between(from, to))
            .fetchOne() ?: 0L
    }

    override fun countRepeatOrderUsers(from: LocalDateTime, to: LocalDateTime): Long {
        return queryFactory
            .select(qOrder.user.id)
            .from(qOrder)
            .where(
                qOrder.status.eq(OrderStatus.COMPLETED),
                qOrder.orderedAt.between(from, to),
            )
            .groupBy(qOrder.user.id)
            .having(qOrder.id.count().gt(1L))
            .fetch()
            .size.toLong()
    }
}
