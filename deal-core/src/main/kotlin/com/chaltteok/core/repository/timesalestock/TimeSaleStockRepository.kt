package com.chaltteok.core.repository.timesalestock

import com.chaltteok.core.domain.TimeSaleStock
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface TimeSaleStockRepository : JpaRepository<TimeSaleStock, Long>, TimeSaleStockRepositoryCustom {
    @Query("SELECT ts FROM TimeSaleStock ts JOIN FETCH ts.product")
    fun findAllWithProduct(): List<TimeSaleStock>

    @Query("SELECT ts FROM TimeSaleStock ts JOIN FETCH ts.product WHERE ts.id = :id")
    fun findByIdWithProduct(id: Long): TimeSaleStock?

    fun findByStockUuid(stockUuid: String): TimeSaleStock?

    @Query("SELECT ts FROM TimeSaleStock ts JOIN FETCH ts.product WHERE ts.stockUuid = :stockUuid")
    fun findByStockUuidWithProduct(stockUuid: String): TimeSaleStock?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ts FROM TimeSaleStock ts JOIN FETCH ts.product WHERE ts.stockUuid = :uuid")
    fun findByStockUuidWithLock(uuid: String): TimeSaleStock?

    @Query("SELECT ts FROM TimeSaleStock ts JOIN FETCH ts.product WHERE ts.status = :status")
    fun findAllByStatusWithProduct(status: TimeSaleStockStatus): List<TimeSaleStock>

    // OPEN + SCHEDULED 상품 반환 (이슈 #95). endAt IS NULL은 무기한 세일 허용.
    @Query("""
        SELECT ts FROM TimeSaleStock ts JOIN FETCH ts.product
        WHERE ts.stockType = 'TIMESALE'
        AND (ts.endAt IS NULL OR ts.endAt >= :now)
        AND ts.status NOT IN (
            com.chaltteok.core.domain.enums.TimeSaleStockStatus.SOLD_OUT,
            com.chaltteok.core.domain.enums.TimeSaleStockStatus.CLOSED
        )
    """)
    fun findVisibleTimeSaleStocks(now: LocalDateTime): List<TimeSaleStock>

    @Query("UPDATE TimeSaleStock ts SET ts.status = com.chaltteok.core.domain.enums.TimeSaleStockStatus.CLOSED WHERE ts.endAt < :now AND ts.status IN (com.chaltteok.core.domain.enums.TimeSaleStockStatus.OPEN, com.chaltteok.core.domain.enums.TimeSaleStockStatus.SOLD_OUT)")
    @Modifying
    fun closeExpiredStocks(now: LocalDateTime): Int

    @Query("UPDATE TimeSaleStock ts SET ts.status = com.chaltteok.core.domain.enums.TimeSaleStockStatus.OPEN WHERE ts.startAt <= :now AND ts.status = com.chaltteok.core.domain.enums.TimeSaleStockStatus.SCHEDULED")
    @Modifying
    fun openScheduledStocks(now: LocalDateTime): Int
}
