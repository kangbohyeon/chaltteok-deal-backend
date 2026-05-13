package com.chaltteok.user.auth.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.LoginRequest
import com.chaltteok.common.security.dto.ReissueRequest
import com.chaltteok.common.security.dto.TokenDto
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.user.auth.dto.RegisterRequest
import com.chaltteok.user.auth.service.UserAuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/auth")
class AuthController(
    private val userAuthService: UserAuthService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseDTO<TokenDto> =
        ResponseDTO.success(userAuthService.login(request.email, request.password))

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseDTO<Unit> {
        userAuthService.register(request)
        return ResponseDTO.success(Unit)
    }

    @PostMapping("/reissue")
    fun reissue(@RequestBody request: ReissueRequest): ResponseDTO<TokenDto> {
        val token = request.refreshToken
        if (!jwtTokenProvider.validateToken(token)) throw BusinessException(AuthErrorCode.INVALID_TOKEN)

        val id = jwtTokenProvider.getIdFromToken(token)
        val storedRole = jwtTokenProvider.getRoleFromToken(token)
        val stored = jwtTokenProvider.getStoredRefreshToken(id, storedRole)
            ?: throw BusinessException(AuthErrorCode.INVALID_TOKEN)

        if (stored != token) throw BusinessException(AuthErrorCode.TOKEN_MISMATCH)

        val newAccess = jwtTokenProvider.generateAccessToken(id, storedRole)
        val newRefresh = jwtTokenProvider.generateRefreshToken(id, storedRole)
        return ResponseDTO.success(TokenDto(newAccess, newRefresh, id))
    }
}
