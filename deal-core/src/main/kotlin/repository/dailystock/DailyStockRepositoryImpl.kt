package repository.dailystock

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class DailyStockRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
):DailStockRepositoryCustom {
}