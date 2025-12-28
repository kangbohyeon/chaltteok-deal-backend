package com.chaltteok.core.repository.product

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) :ProductRepositoryCustom{
}