package com.chaltteok.user.dailystock.service

import com.chaltteok.user.dailystock.dto.OpenDailyStockResponse

interface DailyStockQueryService {
    fun getOpenDailyStocks(): List<OpenDailyStockResponse>
    fun getParticipatedStockIds(userId: Long): List<String>
}
