package com.chaltteok.owner.notification.service

import com.chaltteok.core.repository.notification.NotificationRepository
import com.chaltteok.owner.notification.dto.NotificationListResponse
import com.chaltteok.owner.notification.dto.NotificationResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

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

    @Transactional
    override fun markRead(notificationUuid: String) {
        val notification = notificationRepository.findByNotificationUuid(notificationUuid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다: $notificationUuid")
        notification.isRead = true
    }

    @Transactional
    override fun delete(notificationUuid: String) {
        val deleted = notificationRepository.deleteByNotificationUuid(notificationUuid)
        if (deleted == 0) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다: $notificationUuid")
        }
    }
}
