package com.chaltteok.core.infrastructure.outbox

import com.chaltteok.core.common.KafkaTopics
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

abstract class AbstractOutboxPublisherJob(
    private val outboxEventRepository: OutboxEventRepository,
) {
    abstract val source: String

    protected abstract fun sendToKafka(topic: String, key: String, payload: String): CompletableFuture<*>

    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:3000}")
    @Transactional
    open fun publish() {
        val events = outboxEventRepository.findPendingBatch(
            source = source,
            maxRetry = MAX_RETRIES,
            pageable = PageRequest.of(0, BATCH_SIZE),
        )
        if (events.isEmpty()) return

        // Phase 1: 전체 전송 동시 시작 (직렬 블로킹 제거)
        val futures = events.mapNotNull { event ->
            val topic = KafkaTopics.OUTBOX_TOPIC_MAP[event.eventType]
            if (topic == null) {
                log.error { "알 수 없는 eventType — id=${event.id}, type=${event.eventType}" }
                return@mapNotNull null
            }
            event to sendToKafka(topic, event.aggregateId, event.payload)
        }

        // Phase 2: 결과 수집 (전체 대기 최대 5초)
        val processedIds = mutableListOf<Long>()
        val retryIds = mutableListOf<Long>()
        val failedIds = mutableListOf<Long>()

        futures.forEach { (event, future) ->
            try {
                future.get(5, TimeUnit.SECONDS)
                processedIds += event.id!!
            } catch (ex: Exception) {
                log.warn(ex) { "Outbox 발행 실패 (retryCount=${event.retryCount + 1}) — id=${event.id}, type=${event.eventType}" }
                if (event.retryCount + 1 >= MAX_RETRIES) failedIds += event.id!!
                else retryIds += event.id!!
            }
        }

        // Phase 3: Bulk UPDATE (개별 dirty-check UPDATE 제거, DB 라운드트립 최대 3회)
        val now = LocalDateTime.now()
        if (processedIds.isNotEmpty()) outboxEventRepository.markProcessed(processedIds, now)
        if (retryIds.isNotEmpty()) outboxEventRepository.incrementRetry(retryIds)
        if (failedIds.isNotEmpty()) outboxEventRepository.markFailed(failedIds)

        log.debug { "Outbox 배치 처리 완료 — 총 ${events.size}건 (성공=${processedIds.size}, 재시도=${retryIds.size}, 실패=${failedIds.size})" }
    }

    companion object {
        const val MAX_RETRIES = 3
        const val BATCH_SIZE = 100
    }
}
