package repository.systemauditlog

import domain.SystemAuditLog
import org.springframework.data.jpa.repository.JpaRepository

interface SystemAuditLogRepository : JpaRepository<SystemAuditLog, Long>, SystemAuditLogRepositoryCustom {
}