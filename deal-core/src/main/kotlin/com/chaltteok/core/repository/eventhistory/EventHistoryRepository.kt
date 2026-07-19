package com.chaltteok.core.repository.eventhistory

import com.chaltteok.core.domain.EventHistory
import com.chaltteok.core.domain.TimeSaleStock
import com.chaltteok.core.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EventHistoryRepository : JpaRepository<EventHistory, Long>, EventHistoryRepositoryCustom {
    fun findAllByUser_Id(userId: Long): List<EventHistory>
    fun countByUser_IdAndTimeSaleStock_Id(userId: Long, timeSaleStockId: Long?): Long

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM EventHistory eh WHERE eh.timeSaleStock = :timeSaleStock")
    fun deleteAllByTimeSaleStock(@Param("timeSaleStock") timeSaleStock: TimeSaleStock)

    @Query("SELECT eh.timeSaleStock.stockUuid FROM EventHistory eh WHERE eh.user.id = :userId")
    fun findStockUuidsByUserId(@Param("userId") userId: Long): List<String>

    @Query("SELECT eh FROM EventHistory eh JOIN FETCH eh.timeSaleStock WHERE eh.user.id = :userId")
    fun findAllWithStockByUserId(@Param("userId") userId: Long): List<EventHistory>
}
