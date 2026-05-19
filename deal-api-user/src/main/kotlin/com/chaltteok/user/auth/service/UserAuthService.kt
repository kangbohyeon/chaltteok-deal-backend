package com.chaltteok.user.auth.service

import com.chaltteok.common.security.dto.LoginResponseDto
import com.chaltteok.user.auth.dto.RegisterRequest

interface UserAuthService {
    fun login(email: String, password: String): LoginResponseDto
    fun register(request: RegisterRequest)
    fun existsByEmail(email: String): Boolean
}
