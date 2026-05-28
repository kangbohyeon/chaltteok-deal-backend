package com.chaltteok.common.security.dto

class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val userUuid: String,
    val requirePasswordChange: Boolean = false,
)
