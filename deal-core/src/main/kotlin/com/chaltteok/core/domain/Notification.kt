package com.chaltteok.core.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "tb_notifications", uniqueConstraints = [UniqueConstraint(name = "uk_notification_uuid", columnNames = ["notification_uuid"])])
class Notification(
    @Column(name = "type", nullable = false, length = 30)
    val type: String,

    @Column(name = "title", nullable = false, length = 100)
    val title: String,

    @Column(name = "message", nullable = false, length = 500)
    val message: String,

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    var id: Long? = null

    @Column(name = "notification_uuid", nullable = false, length = 36)
    val notificationUuid: String = UUID.randomUUID().toString()
}
