package com.chaltteok.owner.sse

import com.chaltteok.core.common.KafkaTopics
import com.chaltteok.core.event.OrderCompletedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class OrderNotificationKafkaListener(
    private val sseService: OrderNotificationSseService,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(
        topics = [KafkaTopics.ORDER_COMPLETED],
        groupId = "deal-owner-sse-group",
        containerFactory = "sseKafkaListenerContainerFactory",
    )
    fun consume(message: String) {
        val event = objectMapper.readValue(message, OrderCompletedEvent::class.java)
        log.info { "주문 완료 이벤트 수신 → SSE 브로드캐스트 — orderNumber=${event.orderNumber}" }
        sseService.broadcast(
            eventName = "order-confirmed",
            data = mapOf(
                "orderNumber" to event.orderNumber,
                "userName" to event.userName,
                "productName" to event.productName,
                "totalAmount" to event.totalAmount,
            )
        )
    }
}
