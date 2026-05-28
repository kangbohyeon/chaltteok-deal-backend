package com.chaltteok.user.dailystock.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.dailystock.dto.OpenDailyStockResponse
import com.chaltteok.user.dailystock.service.DailyStockQueryService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/daily-stocks")
class DailyStockQueryController(
    private val dailyStockQueryService: DailyStockQueryService,
) {
    @GetMapping("/open")
    fun getOpenDailyStocks(): ResponseDTO<List<OpenDailyStockResponse>> =
        ResponseDTO.success(dailyStockQueryService.getOpenDailyStocks())

    @GetMapping("/participated")
    fun getParticipatedStockIds(authentication: Authentication): ResponseDTO<List<Long>> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(dailyStockQueryService.getParticipatedStockIds(userId))
    }
}
