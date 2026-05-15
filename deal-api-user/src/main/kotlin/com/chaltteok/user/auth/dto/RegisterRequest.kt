package com.chaltteok.user.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class RegisterRequest(
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank
    @field:Size(min = 10, max = 255)
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,255}$",
        message = "비밀번호는 10자 이상, 대소문자·숫자·특수문자를 각 1개 이상 포함해야 합니다."
    )
    val password: String,
    @field:NotBlank val name: String,
    @field:NotBlank val phone: String,
)
