package com.chaltteok.user.profile.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ChangePasswordRequest(
    val currentPassword: String?,

    @field:NotBlank
    @field:Size(min = 10, message = "비밀번호는 10자 이상이어야 합니다.")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{10,}\$",
        message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각 1개 이상 포함해야 합니다."
    )
    val newPassword: String,
)
