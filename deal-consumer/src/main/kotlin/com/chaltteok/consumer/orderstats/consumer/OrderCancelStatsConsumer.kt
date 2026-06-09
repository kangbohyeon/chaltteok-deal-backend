package com.chaltteok.consumer.orderstats.consumer

import com.chaltteok.core.event.OrderCancelledEvent
import com.chaltteok.core.service.orderstats.OrderStatsService
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val log = KotlinLogging.logger {}
private val CANCELLED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

@Component
class OrderCancelStatsConsumer(
    private val orderStatsService: OrderStatsService,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(
        topics = ["order-cancelled-events"],
        groupId = "deal-cancel-stats-group",
        concurrency = "3",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun consume(message: String) {
        val event = objectMapper.readValue(message, OrderCancelledEvent::class.java)
        log.info { "주문취소 통계 이벤트 수신 — orderNumber=${event.orderNumber}" }
        val date = LocalDateTime.parse(event.cancelledAt, CANCELLED_AT_FORMATTER).toLocalDate()
        orderStatsService.incrementCancelStats(date)
    }
}
