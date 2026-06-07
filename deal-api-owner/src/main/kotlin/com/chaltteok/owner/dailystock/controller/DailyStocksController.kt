package com.chaltteok.owner.dailystock.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.dailystock.dto.DailyStocksRegisterRequest
import com.chaltteok.owner.dailystock.dto.OwnerDailyStockListResponse
import com.chaltteok.owner.dailystock.service.DailyStockService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/daily-stocks")
class DailyStocksController(
    private val dailyStockService: DailyStockService
) {
    @GetMapping
    fun getDailyStocks(): ResponseEntity<ResponseDTO<List<OwnerDailyStockListResponse>>> =
        ResponseEntity.ok(ResponseDTO.success(dailyStockService.findAllDailyStocks()))

    @PostMapping
    fun createDailyStock(@Valid @RequestBody dailyStocksRegisterRequest: DailyStocksRegisterRequest)
            : ResponseEntity<ResponseDTO<Any>> {
        dailyStockService.registerDailyStock(dailyStocksRegisterRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success())
    }

    @PutMapping("/{stockUuid}")
    fun updateDailyStock(
        @PathVariable stockUuid: String,
        @Valid @RequestBody request: DailyStocksRegisterRequest,
    ): ResponseEntity<ResponseDTO<Any>> {
        dailyStockService.updateDailyStock(stockUuid, request)
        return ResponseEntity.ok(ResponseDTO.success())
    }

    @DeleteMapping("/{stockUuid}")
    fun deleteDailyStock(@PathVariable stockUuid: String): ResponseEntity<ResponseDTO<Any>> {
        dailyStockService.deleteDailyStock(stockUuid)
        return ResponseEntity.ok(ResponseDTO.success())
    }
}