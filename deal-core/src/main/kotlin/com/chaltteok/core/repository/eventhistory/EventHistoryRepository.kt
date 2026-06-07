package com.chaltteok.core.repository.eventhistory

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.EventHistory
import com.chaltteok.core.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface EventHistoryRepository : JpaRepository<EventHistory, Long>, EventHistoryRepositoryCustom {
    fun findByUserAndDailyStock(user: User, dailyStock: DailyStock): EventHistory?
    fun findAllByUser_Id(userId: Long): List<EventHistory>
    fun existsByUser_IdAndDailyStock_Id(userId: Long, dailyStockId: Long?): Boolean
    fun countByUser_IdAndDailyStock_Id(userId: Long, dailyStockId: Long?): Long
}