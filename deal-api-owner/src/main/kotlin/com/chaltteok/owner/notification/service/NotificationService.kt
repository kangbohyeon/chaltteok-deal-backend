package com.chaltteok.owner.notification.service

import com.chaltteok.owner.notification.dto.NotificationListResponse

interface NotificationService {
    fun getNotifications(): NotificationListResponse
    fun markAllRead()
}
