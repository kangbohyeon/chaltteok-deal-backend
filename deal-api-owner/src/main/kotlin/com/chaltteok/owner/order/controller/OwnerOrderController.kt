package com.chaltteok.owner.order.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.owner.order.dto.OwnerOrderDetailResponse
import com.chaltteok.owner.order.dto.OwnerOrderListResponse
import com.chaltteok.owner.order.service.OwnerOrderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/owner/orders")
class OwnerOrderController(private val ownerOrderService: OwnerOrderService) {

    @GetMapping
    fun getOrders(
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseDTO<OwnerOrderListResponse> =
        ResponseDTO.success(ownerOrderService.getOrders(status, startDate, endDate, page, size))

    @GetMapping("/{orderNumber}")
    fun getOrderDetail(@PathVariable orderNumber: String): ResponseDTO<OwnerOrderDetailResponse> =
        ResponseDTO.success(ownerOrderService.getOrderDetail(orderNumber))
}
