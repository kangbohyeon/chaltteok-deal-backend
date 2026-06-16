package com.chaltteok.user.auth.dto

import jakarta.validation.constraints.NotBlank

class FindAccountRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val phone: String,
)
