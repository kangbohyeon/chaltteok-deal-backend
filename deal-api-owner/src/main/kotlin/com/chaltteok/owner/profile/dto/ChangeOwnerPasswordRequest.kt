package com.chaltteok.owner.profile.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class ChangeOwnerPasswordRequest(
    val currentPassword: String?,
    @field:NotBlank @field:Size(min = 8) val newPassword: String,
)
