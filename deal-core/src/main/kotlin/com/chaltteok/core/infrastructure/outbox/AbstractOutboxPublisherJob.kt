package com.chaltteok.core.infrastructure.outbox

import com.chaltteok.core.common.KafkaTopics
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

abstract class AbstractOutboxPublisherJob(
    private val outboxEventRepository: OutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    abstract val source: String

    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:3000}")
    @Transactional
    open fun publish() {
        val events = outboxEventRepository.findPendingBatch(
            source = source,
            maxRetry = MAX_RETRIES,
            pageable = PageRequest.of(0, BATCH_SIZE),
        )
        if (events.isEmpty()) return

        // Phase 1: 전체 전송 동시 시작, 건당 5초 타임아웃 부여
        val futures = events.mapNotNull { event ->
            val topic = KafkaTopics.OUTBOX_TOPIC_MAP[event.eventType]
            if (topic == null) {
                log.error { "알 수 없는 eventType — id=${event.id}, type=${event.eventType}" }
                return@mapNotNull null
            }
            event to (kafkaTemplate.send(topic, event.aggregateId, event.payload)
                .orTimeout(5, TimeUnit.SECONDS) as CompletableFuture<*>)
        }

        // Phase 2: 전체 최대 5초 내 완료 대기 (직렬 블로킹 제거 — 최악 100×5s → 5s)
        CompletableFuture.allOf(*futures.map { it.second }.toTypedArray())
            .exceptionally { null }
            .join()

        // Phase 3: 결과 수집 및 Bulk UPDATE (DB 라운드트립 최대 3회)
        val processedIds = mutableListOf<Long>()
        val retryIds = mutableListOf<Long>()
        val failedIds = mutableListOf<Long>()

        futures.forEach { (event, future) ->
            if (future.isCompletedExceptionally || !future.isDone) {
                log.warn { "Outbox 발행 실패 (retryCount=${event.retryCount + 1}) — id=${event.id}, type=${event.eventType}" }
                if (event.retryCount + 1 >= MAX_RETRIES) failedIds += event.id!!
                else retryIds += event.id!!
            } else {
                processedIds += event.id!!
            }
        }

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
