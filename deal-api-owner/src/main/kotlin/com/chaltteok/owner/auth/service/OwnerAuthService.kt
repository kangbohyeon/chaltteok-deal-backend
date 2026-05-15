package com.chaltteok.owner.auth.service

import com.chaltteok.common.security.dto.LoginResponseDto

interface OwnerAuthService {
    fun login(username: String, password: String): LoginResponseDto
}
