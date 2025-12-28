package com.chaltteok.core.repository.systemauditlog

import com.chaltteok.core.domain.SystemAuditLog
import org.springframework.data.jpa.repository.JpaRepository

interface SystemAuditLogRepository : JpaRepository<SystemAuditLog, Long>, SystemAuditLogRepositoryCustom {
}