package com.chaltteok.consumer.order.consumer

import com.chaltteok.consumer.order.dto.OrderEventDto
import com.chaltteok.consumer.order.service.OrderProcessService
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class OrderEventConsumer(
    private val orderProcessService: OrderProcessService,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(topics = ["deal-order-events"], groupId = "deal-order-group", concurrency = "3")
    fun consume(message: String) {
        try {
            val event = objectMapper.readValue(message, OrderEventDto::class.java)
            log.info { "주문 이벤트 수신 — userId=${event.userId}, dailyStockId=${event.dailyStockId}" }
            orderProcessService.processOrder(event.userId, event.dailyStockId)
        } catch (e: Exception) {
            log.error(e) { "주문 이벤트 처리 실패 — message=$message" }
        }
    }
}
