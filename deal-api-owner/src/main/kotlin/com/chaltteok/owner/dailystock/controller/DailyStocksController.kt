package com.chaltteok.owner.dailystock.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.dailystock.dto.DailyStocksRegisterRequest
import com.chaltteok.owner.dailystock.service.DailyStockService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/daily-stocks")
class DailyStocksController(
    private val dailyStockService: DailyStockService
) {
    @PostMapping
    fun createDailyStock(@Valid @RequestBody dailyStocksRegisterRequest: DailyStocksRegisterRequest)
            : ResponseEntity<ResponseDTO<Any>> {
        dailyStockService.registerDailyStock(dailyStocksRegisterRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success());
    }
}