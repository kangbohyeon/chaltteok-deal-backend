package com.chaltteok.core.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "tb_orders",
    indexes = [
        Index(name = "idx_user_orders", columnList = "user_id,ordered_at"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_order_uuid", columnNames = ["order_uuid"])
    ]
)
class Order(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "total_price", nullable = false)
    val totalPrice: Int,

    @Column(name = "status", length = 20)
    var status: String = "PENDING",

    @Column(name = "receiver_name", nullable = false, length = 50)
    val receiverName: String,

    @Column(name = "receiver_phone", nullable = false, length = 20)
    val receiverPhone: String,

    @Column(name = "address", nullable = false, length = 255)
    val address: String,

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
}