package com.chaltteok.consumer.notification

import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.event.StockSoldOutEvent
import com.chaltteok.core.infrastructure.outbox.AbstractOutboxBatchJob
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class StockSoldOutNotificationJob(
    outboxEventRepository: OutboxEventRepository,
    private val persister: SoldOutNotificationPersister,
    private val objectMapper: ObjectMapper,
) : AbstractOutboxBatchJob(outboxEventRepository) {

    override val source = OutboxEvent.SOURCE_CONSUMER_NOTIFICATION

    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:3000}")
    @SchedulerLock(name = "StockSoldOutNotificationJob", lockAtMostFor = "PT30S")
    fun process() {
        executeBatch()
    }

    override fun processBatch(events: List<OutboxEvent>): BatchResult {
        val processedIds = mutableListOf<Long>()
        val retryIds = mutableListOf<Long>()
        val failedIds = mutableListOf<Long>()

        events.forEach { event ->
            val id = requireNotNull(event.id) { "OutboxEvent.id must not be null — source=${event.source}" }
            try {
                val soldOutEvent = objectMapper.readValue(event.payload, StockSoldOutEvent::class.java)
                persister.save(soldOutEvent.productName, id)
                processedIds += id
            } catch (e: DataIntegrityViolationException) {
                // source_event_id UNIQUE 위반 = 이전 실행에서 이미 알림 저장 완료 → PROCESSED로 처리
                log.info { "SOLD_OUT 알림 중복 감지(이미 처리됨) — id=$id" }
                processedIds += id
            } catch (e: Exception) {
                log.warn(e) { "SOLD_OUT 알림 저장 실패 (retryCount=${event.retryCount + 1}) — id=$id" }
                if (isMaxRetryReached(event)) failedIds += id else retryIds += id
            }
        }

        return BatchResult(processedIds, retryIds, failedIds)
    }
}
