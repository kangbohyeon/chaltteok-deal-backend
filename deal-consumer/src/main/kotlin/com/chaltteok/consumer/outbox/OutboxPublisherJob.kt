package com.chaltteok.consumer.outbox

import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

private val TOPIC_MAP = mapOf(
    OutboxEvent.TYPE_ORDER_COMPLETED to "order-completed-events",
    OutboxEvent.TYPE_ORDER_CANCELLED to "order-cancelled-events",
)
private const val MAX_RETRIES = 3
private const val BATCH_SIZE = 100

@Component
class OutboxPublisherJob(
    private val outboxEventRepository: OutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:3000}")
    @Transactional
    fun publish() {
        val events = outboxEventRepository.findPendingBatch(
            source = OutboxEvent.SOURCE_CONSUMER,
            maxRetry = MAX_RETRIES,
            pageable = PageRequest.of(0, BATCH_SIZE),
        )
        if (events.isEmpty()) return

        events.forEach { event ->
            val topic = TOPIC_MAP[event.eventType]
            if (topic == null) {
                event.status = OutboxEvent.STATUS_FAILED
                log.error { "알 수 없는 eventType — id=${event.id}, type=${event.eventType}" }
                return@forEach
            }
            try {
                kafkaTemplate.send(topic, event.aggregateId, event.payload)
                    .get(5, TimeUnit.SECONDS)
                event.status = OutboxEvent.STATUS_PROCESSED
                event.processedAt = LocalDateTime.now()
            } catch (ex: Exception) {
                event.retryCount++
                event.status = if (event.retryCount >= MAX_RETRIES) OutboxEvent.STATUS_FAILED else OutboxEvent.STATUS_PENDING
                log.warn(ex) { "Outbox 발행 실패 (retryCount=${event.retryCount}) — id=${event.id}, type=${event.eventType}" }
            }
        }
        log.debug { "Outbox 배치 처리 완료 — 총 ${events.size}건" }
    }
}
