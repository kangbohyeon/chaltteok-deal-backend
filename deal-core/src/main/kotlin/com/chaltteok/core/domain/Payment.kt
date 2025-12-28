package com.chaltteok.core.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "tb_payments",
    indexes = [
        Index(name = "idx_order_id", columnList = "order_id"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_payment_uuid", columnNames = ["payment_uuid"])
    ]
)
class Payment(
    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    @Column(name = "pg_provider", length = 20)
    var pgProvider: String? = null,

    @Column(name = "pg_tid", length = 100)
    var pgTid: String? = null,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    @Column(name = "status", length = 20)
    var status: String = "READY",

    @Column(name = "paid_at", columnDefinition = "DATETIME")
    val paidAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()


) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    var id: Long? = null

    @Column(name = "payment_uuid", nullable = false, unique = true, length = 36)
    val paymentUuid: String = UUID.randomUUID().toString()
}