package com.chaltteok.consumer.notification

import com.chaltteok.core.domain.Notification
import com.chaltteok.core.domain.enums.NotificationType
import com.chaltteok.core.event.OrderCompletedEvent
import com.chaltteok.core.repository.notification.NotificationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

private val log = KotlinLogging.logger {}

// confirmOrder() 트랜잭션 커밋 후 점주 알림 저장 (Kafka 재발행 없음)
@Component
class OrderNotificationEventListener(private val notificationRepository: NotificationRepository) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handle(event: OrderCompletedEvent) {
        try {
            notificationRepository.save(
                Notification(
                    type = NotificationType.ORDER.name,
                    title = "새 주문이 들어왔습니다",
                    message = "${event.productName} (%,d원)".format(event.totalAmount),
                )
            )
            log.info { "주문 알림 저장 완료 — orderNumber=${event.orderNumber}" }
        } catch (ex: Exception) {
            log.error(ex) { "주문 알림 저장 실패 — orderNumber=${event.orderNumber}" }
        }
    }
}
