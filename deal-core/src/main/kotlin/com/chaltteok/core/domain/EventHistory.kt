package com.chaltteok.core.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "tb_event_history",
    uniqueConstraints = [UniqueConstraint(name = "uk_one_event_per_user", columnNames = ["user_id", "stock_id"])]
)
class EventHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    val dailyStock: DailyStock,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    var id: Long? = null
}
