package com.chaltteok.user.order.dto

import jakarta.validation.constraints.NotBlank

data class OrderRequest(
    @field:NotBlank
    val stockUuid: String,
)
