package com.chaltteok.core.domain

import jakarta.persistence.*
import java.util.UUID
import java.time.LocalDate

@Entity
@Table(
    name = "tb_order_stats",
    uniqueConstraints = [UniqueConstraint(name = "uk_order_stats_date", columnNames = ["stat_date"])]
)
class OrderStats(
    @Column(name = "stat_date", nullable = false)
    val statDate: LocalDate,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    var id: Long? = null

    @Column(name = "stat_uuid", nullable = false, length = 36, updatable = false)
    val statUuid: String = UUID.randomUUID().toString()

    @Column(name = "order_count", nullable = false)
    var orderCount: Long = 0
        private set

    @Column(name = "total_revenue", nullable = false)
    var totalRevenue: Long = 0
        private set

    @Column(name = "cancelled_count", nullable = false)
    var cancelledCount: Long = 0
        private set

    fun recordOrder(revenue: Long) {
        orderCount++
        totalRevenue += revenue
    }

    fun recordCancellation() {
        cancelledCount++
    }
}
