package com.chaltteok.consumer.orderstats

import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.service.orderstats.OrderStatsService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val log = KotlinLogging.logger {}
private val ORDERED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

// confirmOrder() 트랜잭션 커밋 후 통계 집계 (Kafka 재발행 없음)
@Component
class OrderStatsEventListener(private val orderStatsService: OrderStatsService) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: OrderCompletedEvent) {
        try {
            val date = LocalDateTime.parse(event.orderedAt, ORDERED_AT_FORMATTER).toLocalDate()
            orderStatsService.incrementOrderStats(date, event.totalAmount)
            log.info { "주문 통계 집계 완료 — orderNumber=${event.orderNumber}" }
        } catch (ex: Exception) {
            log.error(ex) { "주문 통계 집계 실패 — orderNumber=${event.orderNumber}" }
        }
    }
}
