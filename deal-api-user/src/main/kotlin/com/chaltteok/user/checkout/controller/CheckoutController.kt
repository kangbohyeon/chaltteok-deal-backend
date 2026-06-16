package com.chaltteok.user.checkout.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.checkout.dto.CheckoutRequest
import com.chaltteok.user.checkout.dto.CheckoutResponse
import com.chaltteok.user.checkout.service.CheckoutService
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/checkout")
class CheckoutController(
    private val checkoutService: CheckoutService,
) {
    @PostMapping
    fun checkout(
        authentication: Authentication,
        @Valid @RequestBody request: CheckoutRequest,
    ): ResponseDTO<CheckoutResponse> {
        val userId = authentication.principal as Long
        return ResponseDTO.success(checkoutService.checkout(userId, request))
    }
}
