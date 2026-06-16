package com.chaltteok.owner.notification.service

import com.chaltteok.core.repository.notification.NotificationRepository
import com.chaltteok.owner.notification.dto.NotificationListResponse
import com.chaltteok.owner.notification.dto.NotificationResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
) : NotificationService {

    @Transactional(readOnly = true)
    override fun getNotifications(): NotificationListResponse {
        val notifications = notificationRepository.findTop50ByOrderByCreatedAtDesc()
            .map { NotificationResponse.from(it) }
        val unreadCount = notificationRepository.countByIsReadFalse()
        return NotificationListResponse(
            notifications = notifications,
            unreadCount = unreadCount,
        )
    }

    @Transactional
    override fun markAllRead() {
        notificationRepository.markAllAsRead()
    }
}
