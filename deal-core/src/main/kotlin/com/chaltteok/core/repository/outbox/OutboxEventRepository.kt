package com.chaltteok.core.repository.outbox

import com.chaltteok.core.domain.OutboxEvent
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OutboxEventRepository : JpaRepository<OutboxEvent, Long> {

    @Query(
        "SELECT e FROM OutboxEvent e " +
        "WHERE e.source = :source AND e.status = 'PENDING' AND e.retryCount < :maxRetry " +
        "ORDER BY e.createdAt ASC"
    )
    fun findPendingBatch(
        @Param("source") source: String,
        @Param("maxRetry") maxRetry: Int,
        pageable: Pageable,
    ): List<OutboxEvent>
}
