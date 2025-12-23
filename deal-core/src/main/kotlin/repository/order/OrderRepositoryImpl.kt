package repository.order

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory):OrderRepositoryCustom {
}