package com.chaltteok.core.repository.owner

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class OwnerRepositoryImpl(jpaQueryFactory: JPAQueryFactory):OwnerRepositoryCustom {
}