package com.chaltteok.core.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "tb_order_stats",
    uniqueConstraints = [UniqueConstraint(name = "uk_order_stats_date", columnNames = ["stat_date"])]
)
class OrderStats(
    @Column(name = "stat_date", nullable = false)
    val statDate: LocalDate,

    @Column(name = "order_count", nullable = false)
    var orderCount: Long = 0,

    @Column(name = "total_revenue", nullable = false)
    var totalRevenue: Long = 0,

    @Column(name = "cancelled_count", nullable = false)
    var cancelledCount: Long = 0,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    var id: Long? = null
}
