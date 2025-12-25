package com.chaltteok.core.repository.payment

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory):PaymentRepositoryCustom {
}