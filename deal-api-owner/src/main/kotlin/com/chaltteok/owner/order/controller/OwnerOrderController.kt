package com.chaltteok.owner.order.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.order.dto.OwnerOrderDetailResponse
import com.chaltteok.owner.order.service.OwnerOrderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/orders")
class OwnerOrderController(private val ownerOrderService: OwnerOrderService) {

    @GetMapping("/{orderNumber}")
    fun getOrderDetail(@PathVariable orderNumber: String): ResponseDTO<OwnerOrderDetailResponse> =
        ResponseDTO.success(ownerOrderService.getOrderDetail(orderNumber))
}
