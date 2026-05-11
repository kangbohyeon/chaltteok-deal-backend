package com.chaltteok.user.auth.service

import com.chaltteok.common.security.dto.TokenDto
import com.chaltteok.user.auth.dto.RegisterRequest

interface UserAuthService {
    fun login(email: String, password: String): TokenDto
    fun register(request: RegisterRequest)
}
