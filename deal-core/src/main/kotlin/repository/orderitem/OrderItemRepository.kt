package repository.orderitem

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import repository.order.OrderRepositoryCustom

@Repository
class OrderItemRepository(private val jpaQueryFactory: JPAQueryFactory) : OrderRepositoryCustom {
}