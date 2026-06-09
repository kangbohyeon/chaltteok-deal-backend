package com.chaltteok.core.repository.orderstats

import com.chaltteok.core.domain.OrderStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import jakarta.persistence.LockModeType
import java.time.LocalDate

interface OrderStatsRepository : JpaRepository<OrderStats, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM OrderStats s WHERE s.statDate = :statDate")
    fun findByStatDateWithLock(statDate: LocalDate): OrderStats?

    fun findAllByStatDateBetween(from: LocalDate, to: LocalDate): List<OrderStats>
}
