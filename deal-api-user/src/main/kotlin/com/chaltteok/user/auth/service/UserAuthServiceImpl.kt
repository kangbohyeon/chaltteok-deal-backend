package com.chaltteok.user.auth.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.LoginResponseDto
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.core.domain.User
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.auth.dto.RegisterRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserAuthServiceImpl(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) : UserAuthService {

    companion object {
        private const val ROLE = "ROLE_USER"
        private const val LOCAL_PROVIDER = "LOCAL"
    }

    @Transactional(readOnly = true)
    override fun login(email: String, password: String): LoginResponseDto {
        val user = userRepository.findByEmail(email)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }

        val storedPassword = user.password
            ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        if (!passwordEncoder.matches(password, storedPassword)) {
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        val requirePasswordChange = user.passwordChangedAt == null ||
            user.passwordChangedAt!!.isBefore(LocalDateTime.now().minusDays(90))

        val accessToken = jwtTokenProvider.generateAccessToken(user.id!!, ROLE)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id!!, ROLE)
        return LoginResponseDto(accessToken, refreshToken, user.id!!, requirePasswordChange)
    }

    @Transactional
    override fun register(request: RegisterRequest) {
        if (userRepository.existsByEmail(request.email)) {
            throw BusinessException(AuthErrorCode.DUPLICATE_EMAIL)
        }

        val user = User(
            email = request.email,
            nickname = request.name,
            provider = LOCAL_PROVIDER,
            providerId = request.email,
        )
        user.password = passwordEncoder.encode(request.password)
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    override fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)
}
