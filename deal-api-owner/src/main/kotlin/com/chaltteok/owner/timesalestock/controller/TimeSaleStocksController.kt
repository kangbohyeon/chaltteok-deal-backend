package com.chaltteok.owner.timesalestock.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.timesalestock.dto.OwnerTimeSaleStockListResponse
import com.chaltteok.owner.timesalestock.dto.TimeSaleStocksRegisterRequest
import com.chaltteok.owner.timesalestock.service.TimeSaleStockService
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
@RequestMapping("/api/v1/owner")
class TimeSaleStocksController(
    private val timeSaleStockService: TimeSaleStockService
) {
    // /daily-stocks 는 프론트엔드 기존 호출 경로 호환 alias (#89)
    @GetMapping("/time-sale-stocks", "/daily-stocks")
    fun getTimeSaleStocks(): ResponseEntity<ResponseDTO<List<OwnerTimeSaleStockListResponse>>> =
        ResponseEntity.ok(ResponseDTO.success(timeSaleStockService.findAllTimeSaleStocks()))

    @PostMapping("/time-sale-stocks")
    fun createTimeSaleStock(@Valid @RequestBody request: TimeSaleStocksRegisterRequest)
            : ResponseEntity<ResponseDTO<Any>> {
        timeSaleStockService.registerTimeSaleStock(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success())
    }

    @PutMapping("/time-sale-stocks/{stockUuid}")
    fun updateTimeSaleStock(
        @PathVariable stockUuid: String,
        @Valid @RequestBody request: TimeSaleStocksRegisterRequest,
    ): ResponseEntity<ResponseDTO<Any>> {
        timeSaleStockService.updateTimeSaleStock(stockUuid, request)
        return ResponseEntity.ok(ResponseDTO.success())
    }

    @DeleteMapping("/time-sale-stocks/{stockUuid}")
    fun deleteTimeSaleStock(@PathVariable stockUuid: String): ResponseEntity<ResponseDTO<Any>> {
        timeSaleStockService.deleteTimeSaleStock(stockUuid)
        return ResponseEntity.ok(ResponseDTO.success())
    }
}
