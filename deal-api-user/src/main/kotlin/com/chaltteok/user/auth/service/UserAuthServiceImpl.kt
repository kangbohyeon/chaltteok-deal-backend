package com.chaltteok.user.auth.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.LoginResponseDto
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.core.domain.User
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.auth.dto.RegisterRequest
import com.chaltteok.user.auth.email.UserEmailService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserAuthServiceImpl(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val userEmailService: UserEmailService,
) : UserAuthService {

    companion object {
        private const val ROLE = "ROLE_USER"
        private const val LOCAL_PROVIDER = "LOCAL"
    }

    @Transactional
    override fun login(email: String, password: String): LoginResponseDto {
        val user = userRepository.findByEmail(email)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }

        if (user.lockedAt != null) {
            throw BusinessException(AuthErrorCode.ACCOUNT_LOCKED)
        }

        val storedPassword = user.password
            ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)

        if (!passwordEncoder.matches(password, storedPassword)) {
            val failCount = user.loginFailedCount + 1
            user.loginFailedCount = failCount
            if (failCount >= 5) {
                user.lockedAt = LocalDateTime.now()
            }
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        user.loginFailedCount = 0

        val requirePasswordChange = user.requirePasswordChange ||
            user.passwordChangedAt == null ||
            user.passwordChangedAt!!.isBefore(LocalDateTime.now().minusDays(90))

        val accessToken = jwtTokenProvider.generateAccessToken(user.id!!, ROLE)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id!!, ROLE)
        return LoginResponseDto(accessToken, refreshToken, user.userUuid, requirePasswordChange)
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
        user.phone = request.phone
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    override fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    @Transactional(readOnly = true)
    override fun findAccount(name: String, phone: String): String {
        val user = userRepository.findByNicknameAndPhone(name, phone)
            .orElseThrow { BusinessException(AuthErrorCode.USER_NOT_FOUND) }
        return maskEmail(user.email)
    }

    @Transactional
    override fun resetPassword(email: String, name: String) {
        val user = userRepository.findByEmailAndNickname(email, name)
            .orElseThrow { BusinessException(AuthErrorCode.USER_NOT_FOUND) }
        val tempPassword = generateTempPassword()
        user.password = passwordEncoder.encode(tempPassword)
        user.requirePasswordChange = true
        user.passwordChangedAt = null
        userEmailService.sendPasswordReset(email, tempPassword)
    }

    private fun maskEmail(email: String): String {
        val atIndex = email.indexOf('@')
        if (atIndex <= 1) return email
        val local = email.substring(0, atIndex)
        val domain = email.substring(atIndex)
        val masked = local.first() + "*".repeat(local.length - 1)
        return masked + domain
    }

    private fun generateTempPassword(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789!@#\$"
        return (1..12).map { chars.random() }.joinToString("")
    }
}
