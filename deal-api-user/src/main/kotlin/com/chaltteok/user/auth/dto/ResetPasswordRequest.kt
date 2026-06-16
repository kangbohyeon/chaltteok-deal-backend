package com.chaltteok.user.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class ResetPasswordRequest(
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank val name: String,
)
