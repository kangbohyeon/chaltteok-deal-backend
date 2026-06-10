package com.chaltteok.core.repository.outbox

import com.chaltteok.core.domain.OutboxEvent
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

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

    @Modifying(clearAutomatically = true)
    @Query("UPDATE OutboxEvent e SET e.status = 'PROCESSED', e.processedAt = :now WHERE e.id IN :ids")
    fun markProcessed(@Param("ids") ids: List<Long>, @Param("now") now: LocalDateTime): Int

    @Modifying(clearAutomatically = true)
    @Query("UPDATE OutboxEvent e SET e.retryCount = e.retryCount + 1 WHERE e.id IN :ids")
    fun incrementRetry(@Param("ids") ids: List<Long>): Int

    @Modifying(clearAutomatically = true)
    @Query("UPDATE OutboxEvent e SET e.status = 'FAILED', e.retryCount = e.retryCount + 1 WHERE e.id IN :ids")
    fun markFailed(@Param("ids") ids: List<Long>): Int
}
