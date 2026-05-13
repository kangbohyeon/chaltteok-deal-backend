package com.chaltteok.user.order.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.order.dto.OrderHistoryResponse
import com.chaltteok.user.order.service.OrderQueryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/orders")
class OrderQueryController(
    private val orderQueryService: OrderQueryService,
) {
    @GetMapping
    fun getOrderHistory(
        @RequestHeader("X-User-Id") userId: Long,
    ): ResponseDTO<List<OrderHistoryResponse>> =
        ResponseDTO.success(orderQueryService.getOrderHistory(userId))
}
