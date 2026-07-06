package com.chaltteok.owner.notification.dto

import com.chaltteok.core.domain.Notification
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class NotificationResponse(
    val notificationUuid: String,
    val type: String,
    val title: String,
    val message: String,
    @get:JsonProperty("isRead") val isRead: Boolean,
    val orderNumber: String?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(notification: Notification) = NotificationResponse(
            notificationUuid = notification.notificationUuid,
            type = notification.type.name,
            title = notification.title,
            message = notification.message,
            isRead = notification.isRead,
            orderNumber = notification.orderNumber,
            createdAt = notification.createdAt,
        )
    }
}
