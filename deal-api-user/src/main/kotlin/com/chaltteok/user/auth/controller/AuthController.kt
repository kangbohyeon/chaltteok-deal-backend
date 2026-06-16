package com.chaltteok.user.auth.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.LoginRequest
import com.chaltteok.common.security.dto.LoginResponseDto
import com.chaltteok.common.security.dto.ReissueRequest
import com.chaltteok.common.security.dto.TokenDto
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.auth.dto.FindAccountRequest
import com.chaltteok.user.auth.dto.FindAccountResponse
import com.chaltteok.user.auth.dto.RegisterRequest
import com.chaltteok.user.auth.dto.ResetPasswordRequest
import com.chaltteok.user.auth.service.UserAuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/auth")
class AuthController(
    private val userAuthService: UserAuthService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
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
        val userUuid = userRepository.findById(id)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_TOKEN) }
            .userUuid
        return ResponseDTO.success(TokenDto(newAccess, newRefresh, userUuid))
    }

    @PostMapping("/find-account")
    fun findAccount(@Valid @RequestBody request: FindAccountRequest): ResponseDTO<FindAccountResponse> =
        ResponseDTO.success(FindAccountResponse(userAuthService.findAccount(request.name, request.phone)))

    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordRequest): ResponseDTO<Unit> {
        userAuthService.resetPassword(request.email, request.name)
        return ResponseDTO.success(Unit)
    }
}
