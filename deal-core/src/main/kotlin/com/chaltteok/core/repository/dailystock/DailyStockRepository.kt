package com.chaltteok.core.repository.dailystock

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.enums.DailyStockStatus
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface DailyStockRepository : JpaRepository<DailyStock, Long>, DailStockRepositoryCustom {
    @Query("SELECT ds FROM DailyStock ds JOIN FETCH ds.product")
    fun findAllWithProduct(): List<DailyStock>

    fun findByStockUuid(stockUuid: String): DailyStock?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ds FROM DailyStock ds JOIN FETCH ds.product WHERE ds.stockUuid = :uuid")
    fun findByStockUuidWithLock(uuid: String): DailyStock?

    @Query("SELECT ds FROM DailyStock ds JOIN FETCH ds.product WHERE ds.status = :status")
    fun findAllByStatusWithProduct(status: DailyStockStatus): List<DailyStock>

    @Query("SELECT ds FROM DailyStock ds JOIN FETCH ds.product WHERE ds.status = :status AND ds.startAt <= :now AND ds.endAt >= :now")
    fun findActiveTimeSaleStocks(status: DailyStockStatus, now: LocalDateTime): List<DailyStock>

    @Query("UPDATE DailyStock ds SET ds.status = com.chaltteok.core.domain.enums.DailyStockStatus.CLOSED WHERE ds.endAt < :now AND ds.status = com.chaltteok.core.domain.enums.DailyStockStatus.OPEN")
    @Modifying
    fun closeExpiredStocks(now: LocalDateTime): Int

    @Query("UPDATE DailyStock ds SET ds.status = com.chaltteok.core.domain.enums.DailyStockStatus.OPEN WHERE ds.startAt <= :now AND ds.status = com.chaltteok.core.domain.enums.DailyStockStatus.SCHEDULED")
    @Modifying
    fun openScheduledStocks(now: LocalDateTime): Int
}