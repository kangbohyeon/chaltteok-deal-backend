package com.chaltteok.core.repository.notification

import com.chaltteok.core.domain.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findTop50ByOrderByCreatedAtDesc(): List<Notification>
    fun countByIsReadFalse(): Long

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.isRead = false")
    fun markAllAsRead(): Int
}
