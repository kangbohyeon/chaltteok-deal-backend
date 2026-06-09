package com.chaltteok.consumer.email

import com.chaltteok.core.event.OrderCancelledEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class OrderCancelEmailConsumer(
    private val emailService: EmailService,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(
        topics = ["order-cancelled-events"],
        groupId = "deal-cancel-email-group",
        concurrency = "3",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun consume(message: String) {
        val event = objectMapper.readValue(message, OrderCancelledEvent::class.java)
        val maskedEmail = event.userEmail.replace(Regex("(?<=.{2}).(?=.*@)"), "*")
        log.info { "주문취소 이메일 발송 — orderNumber=${event.orderNumber}, to=$maskedEmail" }
        emailService.sendOrderCancellation(event)
    }
}
