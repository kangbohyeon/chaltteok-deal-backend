package com.chaltteok.consumer.notification.consumer

import com.chaltteok.core.domain.Notification
import com.chaltteok.core.domain.enums.NotificationType
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.repository.notification.NotificationRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class NotificationConsumer(
    private val notificationRepository: NotificationRepository,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(
        topics = ["order-completed-events"],
        groupId = "deal-notification-group",
        concurrency = "3",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun consume(message: String) {
        val event = objectMapper.readValue(message, OrderCompletedEvent::class.java)
        log.info { "주문 알림 이벤트 수신 — orderNumber=${event.orderNumber}" }
        notificationRepository.save(
            Notification(
                type = NotificationType.ORDER.name,
                title = "새 주문이 들어왔습니다",
                message = "${event.productName} (%,d원)".format(event.totalAmount),
                orderNumber = event.orderNumber,
            )
        )
    }
}
