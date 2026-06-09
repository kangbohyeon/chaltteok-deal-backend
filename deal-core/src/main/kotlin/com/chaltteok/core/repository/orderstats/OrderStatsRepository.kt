package com.chaltteok.core.repository.orderstats

import com.chaltteok.core.domain.OrderStats
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface OrderStatsRepository : JpaRepository<OrderStats, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM OrderStats s WHERE s.statDate = :statDate")
    fun findByStatDateWithLock(statDate: LocalDate): OrderStats?

    fun findAllByStatDateBetween(from: LocalDate, to: LocalDate): List<OrderStats>

    @Modifying
    @Query(
        value = """
            INSERT INTO tb_order_stats (stat_uuid, stat_date, order_count, total_revenue, cancelled_count, created_at, updated_at)
            VALUES (:statUuid, :statDate, 1, :revenue, 0, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
                order_count   = order_count + 1,
                total_revenue = total_revenue + :revenue,
                updated_at    = NOW()
        """,
        nativeQuery = true,
    )
    fun upsertOrderStats(
        @Param("statUuid") statUuid: String,
        @Param("statDate") statDate: LocalDate,
        @Param("revenue") revenue: Long,
    )

    @Modifying
    @Query(
        value = """
            INSERT INTO tb_order_stats (stat_uuid, stat_date, order_count, total_revenue, cancelled_count, created_at, updated_at)
            VALUES (:statUuid, :statDate, 0, 0, 1, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
                cancelled_count = cancelled_count + 1,
                updated_at      = NOW()
        """,
        nativeQuery = true,
    )
    fun upsertCancelStats(
        @Param("statUuid") statUuid: String,
        @Param("statDate") statDate: LocalDate,
    )
}
