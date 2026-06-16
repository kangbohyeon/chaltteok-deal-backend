package com.chaltteok.common.security.dto

class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val userUuid: String,
    val requirePasswordChange: Boolean = false,
    val passwordChangeReason: PasswordChangeReason? = null,
)

enum class PasswordChangeReason {
    TEMP_PASSWORD,
    EXPIRED,
}
