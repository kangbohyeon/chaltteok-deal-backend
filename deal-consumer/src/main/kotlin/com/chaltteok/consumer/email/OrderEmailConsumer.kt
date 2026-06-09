package com.chaltteok.consumer.email

import com.chaltteok.core.event.OrderCompletedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class OrderEmailConsumer(
    private val emailService: EmailService,
    private val objectMapper: ObjectMapper,
) {
    // try-catch 없음 — KafkaConfig.kafkaListenerContainerFactory의 DLQ 핸들러가 3회 재시도 후 .DLT로 이동
    @KafkaListener(
        topics = ["order-completed-events"],
        groupId = "deal-email-group",
        concurrency = "3",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun consume(message: String) {
        val event = objectMapper.readValue(message, OrderCompletedEvent::class.java)
        log.info { "주문 확인 이메일 발송 — orderNumber=${event.orderNumber}, to=${event.userEmail}" }
        emailService.sendOrderConfirmation(event)
    }
}
