package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.OrderStatus
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Entity
@Table(
    name = "tb_orders",
    indexes = [
        Index(name = "idx_user_orders", columnList = "user_id,ordered_at"),
        Index(name = "idx_order_number", columnList = "order_number"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_order_uuid", columnNames = ["order_uuid"]),
        UniqueConstraint(name = "uk_order_number", columnNames = ["order_number"]),
    ]
)
class Order(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "total_price", nullable = false)
    val totalPrice: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    var status: OrderStatus = OrderStatus.PENDING,

    @Column(name = "receiver_name", nullable = false, length = 50)
    val receiverName: String = "",

    @Column(name = "receiver_phone", nullable = false, length = 20)
    val receiverPhone: String = "",

    @Column(name = "address", nullable = false, length = 255)
    val address: String = "",

    @Column(name = "ordered_at", nullable = false, updatable = false)
    val orderedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    var id: Long? = null

    @Column(name = "order_uuid", nullable = false, unique = true, length = 36)
    val orderUuid: String = UUID.randomUUID().toString()

    // 사용자 노출용 주문번호 (UUID보다 짧고 가독성 있는 식별자)
    @Column(name = "order_number", nullable = false, unique = true, length = 20)
    val orderNumber: String = run {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val suffix = UUID.randomUUID().toString().replace("-", "").uppercase().take(6)
        "ORD$date$suffix"
    }

    fun isCancellable(): Boolean = status == OrderStatus.PENDING || status == OrderStatus.COMPLETED

    fun cancel() {
        status = OrderStatus.CANCELLED
        updatedAt = LocalDateTime.now()
    }
}
