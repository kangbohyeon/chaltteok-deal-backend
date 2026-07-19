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

    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:3000}")
    @Transactional
    open fun publish() {
        executeBatch()
    }

    override fun processBatch(events: List<OutboxEvent>): BatchResult {
        val futures = events.mapNotNull { event ->
            val topic = KafkaTopics.OUTBOX_TOPIC_MAP[event.eventType]
            if (topic == null) {
                log.error { "알 수 없는 eventType — id=${event.id}, type=${event.eventType}" }
                return@mapNotNull null
            }
            event to kafkaTemplate.send(topic, event.aggregateId, event.payload)
                .orTimeout(5, TimeUnit.SECONDS)
        }

        // 하나가 실패해도 나머지 future들이 모두 완료될 때까지 대기 (Race Condition 방지)
        CompletableFuture.allOf(
            *futures.map { (_, f) -> f.exceptionally { null } }.toTypedArray()
        ).join()

        val processedIds = mutableListOf<Long>()
        val retryIds = mutableListOf<Long>()
        val failedIds = mutableListOf<Long>()

        futures.forEach { (event, future) ->
            if (future.isCompletedExceptionally) {
                log.warn { "Outbox 발행 실패 (retryCount=${event.retryCount + 1}) — id=${event.id}, type=${event.eventType}" }
                if (event.retryCount + 1 >= MAX_RETRIES) failedIds += event.id!! else retryIds += event.id!!
            } else {
                processedIds += event.id!!
            }
        }

        return BatchResult(processedIds, retryIds, failedIds)
    }
}
