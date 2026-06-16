package com.chaltteok.consumer.orderstats.consumer

import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.service.orderstats.OrderStatsService
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class OrderStatsConsumer(
    private val orderStatsService: OrderStatsService,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(
        topics = ["order-completed-events"],
        groupId = "deal-order-stats-group",
        concurrency = "3",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun consume(message: String) {
        val event = objectMapper.readValue(message, OrderCompletedEvent::class.java)
        log.info { "주문 통계 이벤트 수신 — orderNumber=${event.orderNumber}" }
        val date = event.orderedAt.toLocalDate()
        orderStatsService.incrementOrderStats(date, event.totalAmount)
    }
}
