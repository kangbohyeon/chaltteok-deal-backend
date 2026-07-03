package com.chaltteok.user.order.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.order.dto.OrderResponse
import com.chaltteok.user.order.dto.OrderHistoryPageResponse
import com.chaltteok.user.order.dto.OrderHistoryResponse
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.service.OrderService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    ): ResponseEntity<ResponseDTO<OrderResponse>> {
        val userId = authentication.principal as Long
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.success(orderService.placeOrder(userId, request)))
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
        val safeKeyword = keyword?.trim()?.take(100)
        val pageable = PageRequest.of(page.coerceIn(0, MAX_PAGE), size.coerceIn(1, 50))
        return ResponseDTO.success(
            orderService.getOrderHistory(userId, safeKeyword, status, fromDate, toDate, paymentStatus, pageable)
        )
    }

    @GetMapping("/{orderNumber}")
    fun getOrderDetail(
        authentication: Authentication,
        @PathVariable orderNumber: String,
    ): ResponseDTO<OrderHistoryResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(orderService.getOrderDetail(userId, orderNumber))
    }

    companion object {
        private const val MAX_PAGE = 9_999
    }
}
