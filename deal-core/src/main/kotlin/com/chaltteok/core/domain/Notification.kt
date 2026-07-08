package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.NotificationType
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "tb_notifications", uniqueConstraints = [UniqueConstraint(name = "uk_notification_uuid", columnNames = ["notification_uuid"])])
class Notification(
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    val type: NotificationType,

    @Column(name = "title", nullable = false, length = 100)
    val title: String,

    @Column(name = "message", nullable = false, length = 500)
    val message: String,

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "order_number", nullable = true, length = 50)
    val orderNumber: String? = null,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    var id: Long? = null

    @Column(name = "notification_uuid", nullable = false, length = 36)
    val notificationUuid: String = UUID.randomUUID().toString()

    companion object {
        fun forOrder(orderNumber: String, amount: Long) = Notification(
            type = NotificationType.ORDER,
            title = "새 주문이 들어왔습니다",
            message = "$orderNumber (%,d원)".format(amount),
            orderNumber = orderNumber,
        )

        fun forSoldOut(productName: String) = Notification(
            type = NotificationType.SOLD_OUT,
            title = "재고가 소진되었습니다",
            message = productName,
        )
    }
}
