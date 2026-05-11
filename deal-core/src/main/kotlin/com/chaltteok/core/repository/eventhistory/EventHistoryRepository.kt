package com.chaltteok.core.repository.eventhistory

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.EventHistory
import com.chaltteok.core.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface EventHistoryRepository : JpaRepository<EventHistory, Long>, EventHistoryRepositoryCustom {
    fun findByUserAndDailyStock(user: User, dailyStock: DailyStock): EventHistory?
}