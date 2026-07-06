package com.chaltteok.owner.sse

import com.chaltteok.core.common.KafkaTopics
import com.chaltteok.core.domain.Notification
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.repository.notification.NotificationRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Component
class OrderNotificationKafkaListener(
    private val sseService: OrderNotificationSseService,
    private val notificationRepository: NotificationRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    @KafkaListener(
        topics = [KafkaTopics.ORDER_COMPLETED],
        groupId = "deal-owner-sse-group",
        containerFactory = "sseKafkaListenerContainerFactory",
    )
    fun consume(message: String) {
        val event = objectMapper.readValue(message, OrderCompletedEvent::class.java)
        // DB 저장 완료 후 SSE 브로드캐스트 — race condition 방지
        notificationRepository.save(Notification.forOrder(event.orderNumber, event.totalAmount))
        log.info { "주문 알림 저장 및 SSE 브로드캐스트 — orderNumber=${event.orderNumber}" }
        sseService.broadcast(
            eventName = "order-confirmed",
            data = mapOf(
                "orderNumber" to event.orderNumber,
                "productName" to event.productName,
                "totalAmount" to event.totalAmount,
            )
        )
    }
}
