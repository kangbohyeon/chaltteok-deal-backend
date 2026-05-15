package com.chaltteok.common.security.dto

class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val requirePasswordChange: Boolean = false,
)
