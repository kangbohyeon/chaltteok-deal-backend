package com.chaltteok.user.auth.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.LoginRequest
import com.chaltteok.common.security.dto.LoginResponseDto
import com.chaltteok.common.security.dto.ReissueRequest
import com.chaltteok.common.security.dto.TokenDto
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.user.auth.dto.RegisterRequest
import com.chaltteok.user.auth.service.UserAuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/auth")
class AuthController(
    private val userAuthService: UserAuthService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseDTO<LoginResponseDto> =
        ResponseDTO.success(userAuthService.login(request.username, request.password))

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseDTO<Unit> {
        userAuthService.register(request)
        return ResponseDTO.success(Unit)
    }

    @GetMapping("/check-email")
    fun checkEmail(@RequestParam email: String): ResponseDTO<Map<String, Boolean>> =
        ResponseDTO.success(mapOf("duplicate" to userAuthService.existsByEmail(email)))

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
