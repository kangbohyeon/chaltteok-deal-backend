package com.chaltteok.user.profile.dto

import jakarta.validation.constraints.NotBlank

data class UpdateNicknameRequest(
    @field:NotBlank val nickname: String,
)
