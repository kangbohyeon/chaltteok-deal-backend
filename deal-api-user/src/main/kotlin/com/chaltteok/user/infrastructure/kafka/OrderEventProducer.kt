package com.chaltteok.user.infrastructure.kafka

import com.chaltteok.core.domain.enums.PaymentMethod
import com.chaltteok.core.event.OrderPlacedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

private const val TOPIC = "deal-order-events"

@Component
class OrderEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun sendOrderEvent(userId: Long, dailyStockId: Long, paymentMethod: PaymentMethod) {
        val payload = objectMapper.writeValueAsString(
            OrderPlacedEvent(userId = userId, dailyStockId = dailyStockId, paymentMethod = paymentMethod)
        )
        kafkaTemplate.send(TOPIC, userId.toString(), payload)
            .whenComplete { result, ex ->
                if (ex != null) {
                    log.error(ex) { "Kafka 발행 실패 — userId=$userId, dailyStockId=$dailyStockId" }
                } else {
                    log.info { "Kafka 발행 성공 — offset=${result.recordMetadata.offset()}" }
                }
            }
    }
}
