package com.chaltteok.user.order.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.dto.OrderResponse
import com.chaltteok.user.order.service.OrderAsyncService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/orders/async")
class OrderAsyncController(
    private val orderAsyncService: OrderAsyncService,
) {
    @PostMapping
    fun placeOrderAsync(
        authentication: Authentication,
        @RequestBody @Valid request: OrderRequest,
    ): ResponseEntity<ResponseDTO<OrderResponse>> {
        val userId = authentication.principal as Long
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(ResponseDTO.success(orderAsyncService.placeOrderAsync(userId, request)))
    }
}
