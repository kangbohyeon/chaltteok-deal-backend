package com.chaltteok.owner.notification.dto

class NotificationListResponse(
    val notifications: List<NotificationResponse>,
    val unreadCount: Long,
)
