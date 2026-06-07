package com.chaltteok.owner.dailystock.service

import com.chaltteok.owner.dailystock.dto.DailyStocksRegisterRequest
import com.chaltteok.owner.dailystock.dto.OwnerDailyStockListResponse

interface DailyStockService {
    fun registerDailyStock(dailyStocksRegisterRequest: DailyStocksRegisterRequest)
    fun findAllDailyStocks(): List<OwnerDailyStockListResponse>
    fun deleteDailyStock(stockUuid: String)
    fun updateDailyStock(stockUuid: String, request: DailyStocksRegisterRequest)
}