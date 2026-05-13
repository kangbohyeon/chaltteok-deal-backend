package com.chaltteok.user.order.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    fun placeOrder(
        @RequestHeader("X-User-Id") userId: Long,
        @RequestBody @Valid request: OrderRequest,
    ): ResponseDTO<String> {
        orderService.placeOrder(userId, request)
        return ResponseDTO.success("주문 요청이 접수되었습니다.")
    }

    @PostMapping("/{orderNumber}/cancel")
    fun cancelOrder(
        @RequestHeader("X-User-Id") userId: Long,
        @PathVariable orderNumber: String,
    ): ResponseDTO<String> {
        orderService.cancelOrder(userId, orderNumber)
        return ResponseDTO.success("주문이 취소되었습니다.")
    }
}
