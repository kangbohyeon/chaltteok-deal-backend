package com.chaltteok.core.repository.systemauditlog

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class SystemAuditLogRepositoryCustomImpl(private val jpaQueryFactory: JPAQueryFactory) : SystemAuditLogRepositoryCustom {
}