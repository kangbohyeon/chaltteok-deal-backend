package com.chaltteok.owner.dailystock.service

import com.chaltteok.owner.dailystock.dto.DailyStocksRegisterRequest

interface DailyStockService {
    fun registerDailyStock(dailyStocksRegisterRequest: DailyStocksRegisterRequest)
}