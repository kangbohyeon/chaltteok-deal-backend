package com.chaltteok.owner.auth.service

import com.chaltteok.common.security.dto.TokenDto

interface OwnerAuthService {
    fun login(username: String, password: String): TokenDto
}
