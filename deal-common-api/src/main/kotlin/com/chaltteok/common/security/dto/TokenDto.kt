package com.chaltteok.common.security.dto

data class TokenDto(
    val accessToken: String,
    val refreshToken: String,
    val userUuid: String,
)
