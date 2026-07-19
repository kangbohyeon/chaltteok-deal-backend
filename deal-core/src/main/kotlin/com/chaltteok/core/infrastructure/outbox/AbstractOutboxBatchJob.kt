package com.chaltteok.core.infrastructure.outbox

import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

abstract class AbstractOutboxBatchJob(
    protected val outboxEventRepository: OutboxEventRepository,
) {
    abstract val source: String

    protected abstract fun processBatch(events: List<OutboxEvent>): BatchResult

    protected fun executeBatch() {
        val events = outboxEventRepository.findPendingBatch(
            source = source,
            maxRetry = MAX_RETRIES,
            pageable = PageRequest.of(0, BATCH_SIZE),
        )
        if (events.isEmpty()) return

        val (processedIds, retryIds, failedIds) = processBatch(events)

        val now = LocalDateTime.now()
        if (processedIds.isNotEmpty()) outboxEventRepository.markProcessed(processedIds, now)
        if (retryIds.isNotEmpty()) outboxEventRepository.incrementRetry(retryIds)
        if (failedIds.isNotEmpty()) outboxEventRepository.markFailed(failedIds)

        log.debug { "Outbox 배치 처리 완료 — source=$source 총 ${events.size}건 (성공=${processedIds.size}, 재시도=${retryIds.size}, 실패=${failedIds.size})" }
    }

    data class BatchResult(
        val processedIds: List<Long>,
        val retryIds: List<Long>,
        val failedIds: List<Long>,
    )

    companion object {
        const val MAX_RETRIES = 3
        const val BATCH_SIZE = 100
    }
}
