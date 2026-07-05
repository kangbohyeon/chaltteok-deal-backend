package com.chaltteok.owner.timesalestock.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.timesalestock.dto.OwnerTimeSaleStockListResponse
import com.chaltteok.owner.timesalestock.service.TimeSaleStockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/daily-stocks")
class DailyStocksController(
    private val timeSaleStockService: TimeSaleStockService,
) {
    @GetMapping
    fun getDailyStocks(): ResponseEntity<ResponseDTO<List<OwnerTimeSaleStockListResponse>>> =
        ResponseEntity.ok(ResponseDTO.success(timeSaleStockService.findAllTimeSaleStocks()))
}
