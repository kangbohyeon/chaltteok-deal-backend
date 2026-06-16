package com.chaltteok.user.dailystock.service

import com.chaltteok.user.dailystock.dto.OpenDailyStockResponse

interface DailyStockQueryService {
    fun getOpenDailyStocks(): List<OpenDailyStockResponse>
    fun getParticipationCounts(userId: Long): Map<String, Int>
}
