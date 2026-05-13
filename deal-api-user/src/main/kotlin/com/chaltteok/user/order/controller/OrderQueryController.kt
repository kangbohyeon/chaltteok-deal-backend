package com.chaltteok.user.order.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.order.dto.OrderHistoryPageResponse
import com.chaltteok.user.order.dto.OrderHistoryResponse
import com.chaltteok.user.order.service.OrderQueryService
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/orders")
class OrderQueryController(
    private val orderQueryService: OrderQueryService,
) {
    @GetMapping
    fun getOrderHistory(
        @RequestHeader("X-User-Id") userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) keyword: String?,
    ): ResponseDTO<OrderHistoryPageResponse> {
        val pageable = PageRequest.of(page, size.coerceIn(1, 50))
        return ResponseDTO.success(orderQueryService.getOrderHistory(userId, keyword, pageable))
    }

    @GetMapping("/{orderNumber}")
    fun getOrderDetail(
        @RequestHeader("X-User-Id") userId: Long,
        @PathVariable orderNumber: String,
    ): ResponseDTO<OrderHistoryResponse> =
        ResponseDTO.success(orderQueryService.getOrderDetail(userId, orderNumber))
}
