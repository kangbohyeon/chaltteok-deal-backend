package com.chaltteok.consumer.notification

import com.chaltteok.core.domain.Notification
import com.chaltteok.core.repository.notification.NotificationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class SoldOutNotificationPersister(
    private val notificationRepository: NotificationRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun save(productName: String, sourceEventId: Long) {
        notificationRepository.save(Notification.forSoldOut(productName, sourceEventId))
    }
}
