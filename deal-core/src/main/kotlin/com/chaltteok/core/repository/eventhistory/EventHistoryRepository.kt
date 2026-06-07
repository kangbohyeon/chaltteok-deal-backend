package com.chaltteok.core.repository.eventhistory

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.EventHistory
import com.chaltteok.core.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EventHistoryRepository : JpaRepository<EventHistory, Long>, EventHistoryRepositoryCustom {
    fun findByUserAndDailyStock(user: User, dailyStock: DailyStock): EventHistory?
    fun findAllByUser_Id(userId: Long): List<EventHistory>
    fun existsByUser_IdAndDailyStock_Id(userId: Long, dailyStockId: Long?): Boolean
    fun countByUser_IdAndDailyStock_Id(userId: Long, dailyStockId: Long?): Long

    @Modifying
    @Query("DELETE FROM EventHistory eh WHERE eh.dailyStock = :dailyStock")
    fun deleteAllByDailyStock(@Param("dailyStock") dailyStock: DailyStock)

    @Query("SELECT eh.dailyStock.stockUuid FROM EventHistory eh WHERE eh.user.id = :userId")
    fun findStockUuidsByUserId(@Param("userId") userId: Long): List<String>
}