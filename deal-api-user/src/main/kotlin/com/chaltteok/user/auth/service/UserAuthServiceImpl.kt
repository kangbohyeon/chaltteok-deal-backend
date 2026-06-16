package com.chaltteok.user.auth.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.LoginResponseDto
import com.chaltteok.common.security.dto.PasswordChangeReason
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.core.domain.User
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.auth.dto.RegisterRequest
import com.chaltteok.user.auth.ratelimit.AccountRecoveryRateLimiter
import com.chaltteok.user.infrastructure.kafka.PasswordResetEmailProducer
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.LocalDateTime

@Service
class UserAuthServiceImpl(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val passwordResetEmailProducer: PasswordResetEmailProducer,
    private val loginFailureRecorder: LoginFailureRecorder,
    private val accountRecoveryRateLimiter: AccountRecoveryRateLimiter,
) : UserAuthService {

    companion object {
        private const val ROLE = "ROLE_USER"
        private const val LOCAL_PROVIDER = "LOCAL"
        private const val PASSWORD_EXPIRY_DAYS = 90L
        private const val LOCK_DURATION_MINUTES = 30L
        private const val TEMP_PASSWORD_LENGTH = 12
    }

    @Transactional
    override fun login(email: String, password: String): LoginResponseDto {
        val user = userRepository.findByEmail(email)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }

        checkAccountLock(user)

        val storedPassword = user.password
            ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)

        if (!passwordEncoder.matches(password, storedPassword)) {
            loginFailureRecorder.recordFailure(user.id!!)
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        loginFailureRecorder.resetFailure(user.id!!)

        val passwordChangeReason = resolvePasswordChangeReason(user)
        // 강제 변경 사유(임시 비밀번호/만료)가 있으면 entity에 영속화해 두어야
        // changePassword가 "이번 요청이 강제 변경 플로우인지"를 판단할 수 있다.
        // 이렇게 하지 않으면 changePassword 쪽에서 만료 여부를 매번 다시 계산하게 되고,
        // 이는 만료된 모든 계정이 currentPassword 검증을 영구적으로 우회하는
        // 보안 취약점으로 이어진다 (탈취된 세션만으로 비밀번호 변경 가능).
        if (passwordChangeReason != null) {
            user.requirePasswordChange = true
        }
        val accessToken = jwtTokenProvider.generateAccessToken(user.id!!, ROLE)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id!!, ROLE)
        return LoginResponseDto(
            accessToken,
            refreshToken,
            user.userUuid,
            requirePasswordChange = passwordChangeReason != null,
            passwordChangeReason = passwordChangeReason,
        )
    }

    private fun checkAccountLock(user: User) {
        val lockedAt = user.lockedAt ?: return
        if (lockedAt.isBefore(LocalDateTime.now().minusMinutes(LOCK_DURATION_MINUTES))) {
            // 잠금 시간 경과 시 자동 해제
            user.lockedAt = null
            user.loginFailedCount = 0
            userRepository.save(user)
            return
        }
        throw BusinessException(AuthErrorCode.ACCOUNT_LOCKED)
    }

    private fun resolvePasswordChangeReason(user: User): PasswordChangeReason? = when {
        user.requirePasswordChange -> PasswordChangeReason.TEMP_PASSWORD
        user.passwordChangedAt == null ||
            user.passwordChangedAt!!.isBefore(LocalDateTime.now().minusDays(PASSWORD_EXPIRY_DAYS)) ->
            PasswordChangeReason.EXPIRED
        else -> null
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
        accountRecoveryRateLimiter.checkAndIncrement(phone)
        val user = userRepository.findByNicknameAndPhone(name, phone)
            .orElseThrow { BusinessException(AuthErrorCode.USER_NOT_FOUND) }
        return maskEmail(user.email)
    }

    @Transactional
    override fun resetPassword(email: String, name: String) {
        accountRecoveryRateLimiter.checkAndIncrement(email)
        val user = userRepository.findByEmailAndNickname(email, name)
            .orElseThrow { BusinessException(AuthErrorCode.USER_NOT_FOUND) }
        val tempPassword = generateTempPassword()
        user.password = passwordEncoder.encode(tempPassword)
        user.requirePasswordChange = true
        user.passwordChangedAt = null
        // 비밀번호 재설정 성공 시 계정 잠금 해제
        user.lockedAt = null
        user.loginFailedCount = 0
        passwordResetEmailProducer.sendPasswordResetRequested(email, tempPassword)
    }

    private fun maskEmail(email: String): String {
        val atIndex = email.indexOf('@')
        // local part가 1자 이하면 마스킹 의미가 없으므로 원본 그대로 반환
        if (atIndex <= 1) return email
        val local = email.substring(0, atIndex)
        val domain = email.substring(atIndex)
        val masked = local.first() + "*".repeat(local.length - 1)
        return masked + domain
    }

    private fun generateTempPassword(): String {
        // SecureRandom 사용 — 비-CSPRNG(kotlin.random.Random) 예측 가능성 제거
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789!@#\$"
        val random = SecureRandom()
        return (1..TEMP_PASSWORD_LENGTH).map { chars[random.nextInt(chars.length)] }.joinToString("")
    }
}
