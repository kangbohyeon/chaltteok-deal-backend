package com.chaltteok.consumer.order.consumer

import com.chaltteok.consumer.order.service.OrderProcessCommand
import com.chaltteok.consumer.order.service.OrderProcessService
import com.chaltteok.core.event.OrderPlacedEvent
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
    // try-catch 없음 — KafkaConfig.kafkaListenerContainerFactory의 DLQ 핸들러가 3회 재시도 후 .DLT로 이동
    @KafkaListener(topics = ["deal-order-events"], groupId = "deal-order-group", concurrency = "3", containerFactory = "kafkaListenerContainerFactory")
    fun consume(message: String) {
        val event = objectMapper.readValue(message, OrderPlacedEvent::class.java)
        log.info { "주문 이벤트 수신 — userId=${event.userId}, timeSaleStockId=${event.timeSaleStockId}" }
        orderProcessService.processOrder(
            OrderProcessCommand(
                userId = event.userId,
                timeSaleStockId = event.timeSaleStockId,
                quantity = event.quantity,
                paymentMethod = event.paymentMethod,
                couponCode = event.couponCode,
            )
        )
    }
}
