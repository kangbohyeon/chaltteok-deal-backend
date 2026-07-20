package com.chaltteok.core.infrastructure.outbox

import com.chaltteok.core.common.KafkaTopics
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

abstract class AbstractOutboxPublisherJob(
    outboxEventRepository: OutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : AbstractOutboxBatchJob(outboxEventRepository) {

    // Spring AOP(@Transactional) 프록싱을 위해 open 필수 — 제거 시 트랜잭션 무효화
    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:3000}")
    @Transactional
    open fun publish() {
        executeBatch()
    }

    override fun processBatch(events: List<OutboxEvent>): BatchResult {
        val kafkaFutures = mutableListOf<Pair<OutboxEvent, CompletableFuture<*>>>()
        val processedIds = mutableListOf<Long>()
        val retryIds = mutableListOf<Long>()
        val failedIds = mutableListOf<Long>()

        events.forEach { event ->
            val id = requireNotNull(event.id) { "OutboxEvent.id must not be null — type=${event.eventType}" }
            val topic = KafkaTopics.OUTBOX_TOPIC_MAP[event.eventType]
            if (topic == null) {
                log.error { "알 수 없는 eventType → FAILED 처리 — id=$id, type=${event.eventType}" }
                failedIds += id
            } else {
                kafkaFutures += event to kafkaTemplate.send(topic, event.aggregateId, event.payload)
                    .orTimeout(5, TimeUnit.SECONDS)
            }
        }

        if (kafkaFutures.isEmpty()) return BatchResult(processedIds, retryIds, failedIds)

        // 하나가 실패해도 나머지 future들이 모두 완료될 때까지 대기 (Race Condition 방지)
        CompletableFuture.allOf(
            *kafkaFutures.map { (_, f) -> f.exceptionally { null } }.toTypedArray()
        ).join()

        kafkaFutures.forEach { (event, future) ->
            val id = requireNotNull(event.id) { "OutboxEvent.id must not be null — type=${event.eventType}" }
            if (future.isCompletedExceptionally) {
                log.warn { "Outbox 발행 실패 (retryCount=${event.retryCount + 1}) — id=$id, type=${event.eventType}" }
                if (isMaxRetryReached(event)) failedIds += id else retryIds += id
            } else {
                processedIds += id
            }
        }

        return BatchResult(processedIds, retryIds, failedIds)
    }
}
