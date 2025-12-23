package repository.orderitem

import com.querydsl.jpa.impl.JPAQueryFactory

class OrderItemRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory): OrderItemRepositoryCustom {
}