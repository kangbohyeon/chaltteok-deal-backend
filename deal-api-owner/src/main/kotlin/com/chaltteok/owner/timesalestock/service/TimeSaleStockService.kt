package com.chaltteok.owner.timesalestock.service

import com.chaltteok.owner.timesalestock.dto.OwnerTimeSaleStockListResponse
import com.chaltteok.owner.timesalestock.dto.TimeSaleStocksRegisterRequest

interface TimeSaleStockService {
    fun registerTimeSaleStock(request: TimeSaleStocksRegisterRequest)
    fun findAllTimeSaleStocks(): List<OwnerTimeSaleStockListResponse>
    fun findTimeSaleStock(stockUuid: String): OwnerTimeSaleStockListResponse
    fun deleteTimeSaleStock(stockUuid: String)
    fun updateTimeSaleStock(stockUuid: String, request: TimeSaleStocksRegisterRequest)
}
