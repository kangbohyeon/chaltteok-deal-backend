package com.chaltteok.consumer.notification

import com.chaltteok.core.domain.Notification
import com.chaltteok.core.repository.notification.NotificationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Component
class NotificationSaveHelper(
    private val notificationRepository: NotificationRepository,
) {
    // 재고 차감 트랜잭션과 독립적으로 실행 — 알림 저장 실패가 재고 차감을 롤백하지 않음
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun save(notification: Notification) {
        try {
            notificationRepository.save(notification)
        } catch (e: Exception) {
            log.error(e) { "알림 저장 실패 — 재고 차감에는 영향 없음: ${notification.message}" }
        }
    }
}
