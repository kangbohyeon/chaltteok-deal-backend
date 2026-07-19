package com.chaltteok.consumer.notification

import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.event.StockSoldOutEvent
import com.chaltteok.core.repository.outbox.OutboxEventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.dao.DataIntegrityViolationException
import io.github.oshai.kotlinlogging.KotlinLogging
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Component
class StockSoldOutNotificationJob(
    private val outboxEventRepository: OutboxEventRepository,
    private val persister: SoldOutNotificationPersister,
    private val objectMapper: ObjectMapper,
) {
    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:3000}")
    @SchedulerLock(name = "StockSoldOutNotificationJob", lockAtMostFor = "PT30S")
    fun process() {
        val events = outboxEventRepository.findPendingBatch(
            source = OutboxEvent.SOURCE_CONSUMER_NOTIFICATION,
            maxRetry = MAX_RETRIES,
            pageable = PageRequest.of(0, BATCH_SIZE),
        )
        if (events.isEmpty()) return

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
                if (event.retryCount + 1 >= MAX_RETRIES) failedIds += id
                else retryIds += id
            }
        }

        val now = LocalDateTime.now()
        if (processedIds.isNotEmpty()) outboxEventRepository.markProcessed(processedIds, now)
        if (retryIds.isNotEmpty()) outboxEventRepository.incrementRetry(retryIds)
        if (failedIds.isNotEmpty()) outboxEventRepository.markFailed(failedIds)

        log.debug { "SOLD_OUT 알림 배치 완료 — 총 ${events.size}건 (성공=${processedIds.size}, 재시도=${retryIds.size}, 실패=${failedIds.size})" }
    }

    companion object {
        const val MAX_RETRIES = 3
        const val BATCH_SIZE = 100
    }
}
