package com.chaltteok.user.checkout.service

import com.chaltteok.user.checkout.dto.CheckoutRequest
import com.chaltteok.user.checkout.dto.CheckoutResponse

interface CheckoutService {
    fun checkout(userId: Long, request: CheckoutRequest): CheckoutResponse
}
