package com.chaltteok.core.repository.eventhistory

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class EventHistoryRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory):EventHistoryRepositoryCustom {
}