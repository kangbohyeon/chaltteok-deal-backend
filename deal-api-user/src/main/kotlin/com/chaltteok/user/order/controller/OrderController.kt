package com.chaltteok.user.order.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.checkout.dto.CheckoutResponse
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    fun placeOrder(
        authentication: Authentication,
        @RequestBody @Valid request: OrderRequest,
    ): ResponseDTO<CheckoutResponse> {
        val userId = authentication.principal as Long
        val result = orderService.placeOrder(userId, request)
        return ResponseDTO.success(result)
    }

    @PostMapping("/{orderNumber}/cancel")
    fun cancelOrder(
        authentication: Authentication,
        @PathVariable orderNumber: String,
    ): ResponseDTO<String> {
        val userId = authentication.principal as Long
        orderService.cancelOrder(userId, orderNumber)
        return ResponseDTO.success("주문이 취소되었습니다.")
    }
}
