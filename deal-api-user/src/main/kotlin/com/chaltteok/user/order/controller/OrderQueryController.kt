package com.chaltteok.user.order.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.order.dto.OrderHistoryPageResponse
import com.chaltteok.user.order.dto.OrderHistoryResponse
import com.chaltteok.user.order.service.OrderQueryService
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/orders")
class OrderQueryController(
    private val orderQueryService: OrderQueryService,
) {
    @GetMapping
    fun getOrderHistory(
        authentication: Authentication,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) fromDate: String?,
        @RequestParam(required = false) toDate: String?,
        @RequestParam(required = false) paymentStatus: String?,
    ): ResponseDTO<OrderHistoryPageResponse> {
        val userId = authentication.principal as Long
        val pageable = PageRequest.of(page, size.coerceIn(1, 50))
        return ResponseDTO.success(
            orderQueryService.getOrderHistory(userId, keyword, status, fromDate, toDate, paymentStatus, pageable)
        )
    }

    @GetMapping("/{orderNumber}")
    fun getOrderDetail(
        authentication: Authentication,
        @PathVariable orderNumber: String,
    ): ResponseDTO<OrderHistoryResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(orderQueryService.getOrderDetail(userId, orderNumber))
    }
}
