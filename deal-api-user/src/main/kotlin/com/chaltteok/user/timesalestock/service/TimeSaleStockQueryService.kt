package com.chaltteok.user.timesalestock.service

import com.chaltteok.user.timesalestock.dto.OpenTimeSaleStockResponse

interface TimeSaleStockQueryService {
    fun getVisibleTimeSaleStocks(): List<OpenTimeSaleStockResponse>
    fun getParticipationCounts(userId: Long): Map<String, Int>
}
