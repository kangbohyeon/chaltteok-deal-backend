package com.chaltteok.owner.auth.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.LoginRequest
import com.chaltteok.common.security.dto.LoginResponseDto
import com.chaltteok.common.security.dto.ReissueRequest
import com.chaltteok.common.security.dto.TokenDto
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.owner.auth.service.OwnerAuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/auth")
class AuthController(
    private val ownerAuthService: OwnerAuthService,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseDTO<LoginResponseDto> =
        ResponseDTO.success(ownerAuthService.login(request.username, request.password))

    @PostMapping("/reissue")
    fun reissue(@Valid @RequestBody request: ReissueRequest): ResponseDTO<TokenDto> {
        val token = request.refreshToken
        if (!jwtTokenProvider.validateToken(token)) throw BusinessException(AuthErrorCode.INVALID_TOKEN)

        val userId = jwtTokenProvider.getIdFromToken(token)
        val role = jwtTokenProvider.getRoleFromToken(token)
        val stored = jwtTokenProvider.getStoredRefreshToken(userId, role)
            ?: throw BusinessException(AuthErrorCode.INVALID_TOKEN)

        if (stored != token) throw BusinessException(AuthErrorCode.TOKEN_MISMATCH)

        val newAccess = jwtTokenProvider.generateAccessToken(userId, role)
        val newRefresh = jwtTokenProvider.generateRefreshToken(userId, role)
        return ResponseDTO.success(TokenDto(newAccess, newRefresh, userId))
    }
}
