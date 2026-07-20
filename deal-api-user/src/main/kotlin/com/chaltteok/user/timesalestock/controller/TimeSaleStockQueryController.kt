package com.chaltteok.user.timesalestock.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.timesalestock.dto.OpenTimeSaleStockResponse
import com.chaltteok.user.timesalestock.service.TimeSaleStockQueryService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/time-sale-stocks")
class TimeSaleStockQueryController(
    private val timeSaleStockQueryService: TimeSaleStockQueryService,
) {
    @GetMapping("/open")
    fun getOpenTimeSaleStocks(): ResponseDTO<List<OpenTimeSaleStockResponse>> =
        ResponseDTO.success(timeSaleStockQueryService.getVisibleTimeSaleStocks())

    @GetMapping("/participated")
    fun getParticipationCounts(authentication: Authentication): ResponseDTO<Map<String, Int>> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(timeSaleStockQueryService.getParticipationCounts(userId))
    }
}
