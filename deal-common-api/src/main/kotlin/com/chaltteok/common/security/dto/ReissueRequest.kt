package com.chaltteok.common.security.dto

import jakarta.validation.constraints.NotBlank

data class ReissueRequest(
    @field:NotBlank val refreshToken: String,
)
