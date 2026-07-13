package com.chaltteok.owner.profile.dto

import jakarta.validation.constraints.NotBlank

data class WithdrawOwnerRequest(
    @field:NotBlank(message = "비밀번호를 입력해주세요")
    val currentPassword: String,
)
