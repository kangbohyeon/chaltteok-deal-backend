package com.chaltteok.owner.controller

import com.chaltteok.common.dto.ResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/daily-stocks")
class DailyStocksController {

    @PostMapping
    fun createDailyStock(@RequestBody): ResponseEntity<ResponseDTO<Any>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success());
    }
}