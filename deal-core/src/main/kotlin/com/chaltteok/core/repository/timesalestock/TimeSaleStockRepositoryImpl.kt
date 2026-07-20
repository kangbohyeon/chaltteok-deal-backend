package com.chaltteok.core.repository.timesalestock

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class TimeSaleStockRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : TimeSaleStockRepositoryCustom {
}
