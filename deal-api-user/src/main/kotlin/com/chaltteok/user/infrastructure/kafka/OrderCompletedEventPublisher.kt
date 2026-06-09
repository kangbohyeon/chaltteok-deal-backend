package com.chaltteok.user.infrastructure.kafka

import com.chaltteok.core.event.OrderCompletedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

private val log = KotlinLogging.logger {}
private const val COMPLETED_TOPIC = "order-completed-events"

@Component
class OrderCompletedEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: OrderCompletedEvent) {
        kafkaTemplate.send(COMPLETED_TOPIC, event.orderNumber, objectMapper.writeValueAsString(event))
            .whenComplete { _, ex ->
                if (ex != null) {
                    log.error(ex) { "주문완료 이벤트 발행 실패 — orderNumber=${event.orderNumber}" }
                } else {
                    log.info { "주문완료 이벤트 발행 성공 — orderNumber=${event.orderNumber}" }
                }
            }
    }
}
