package com.chaltteok.user.profile.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.core.domain.UserConsentHistory
import com.chaltteok.core.repository.consent.UserConsentHistoryRepository
import com.chaltteok.core.repository.consent.UserConsentRepository
import com.chaltteok.core.repository.consentcondition.ConsentConditionRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.profile.dto.ChangePasswordRequest
import com.chaltteok.user.profile.dto.ConsentUpdateRequest
import com.chaltteok.user.profile.dto.UpdateNicknameRequest
import com.chaltteok.user.profile.dto.UserProfileResponse
import com.chaltteok.user.profile.enums.ConsentErrorCode
import com.chaltteok.user.profile.enums.ProfileErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class UserProfileServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userConsentRepository: UserConsentRepository,
    private val userConsentHistoryRepository: UserConsentHistoryRepository,
    private val consentConditionRepository: ConsentConditionRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) : UserProfileService {

    @Transactional(readOnly = true)
    override fun getProfile(userId: Long): UserProfileResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }
        return UserProfileResponse.from(user)
    }

    @Transactional
    override fun updateNickname(userId: Long, request: UpdateNicknameRequest): UserProfileResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }
        user.nickname = request.nickname
        return UserProfileResponse.from(user)
    }

    @Transactional
    override fun changePassword(userId: Long, request: ChangePasswordRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }

        // requirePasswordChange는 로그인 시점에 강제 변경 사유(임시 비밀번호 또는
        // 90일 만료)가 있을 때만 true로 영속화된다 (UserAuthServiceImpl.login 참고).
        // 이 영속화된 플래그만 신뢰해야 한다 — 매 요청마다 만료 여부를 다시 계산하면
        // 만료된 모든 계정이 currentPassword 검증을 영구적으로 우회하게 되어
        // 탈취된 세션만으로 비밀번호를 변경/계정을 장악할 수 있는 취약점이 생긴다.
        val isForcedChange = user.requirePasswordChange

        if (user.password != null && !isForcedChange) {
            if (request.currentPassword.isNullOrBlank() ||
                !passwordEncoder.matches(request.currentPassword, user.password)) {
                throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            }
        }

        user.password = passwordEncoder.encode(request.newPassword)
        user.passwordChangedAt = LocalDateTime.now()
        user.requirePasswordChange = false
    }

    @Transactional
    override fun updateConsent(userId: Long, request: ConsentUpdateRequest) {
        val condition = consentConditionRepository.findByConsentType(request.consentType)
        if (!request.agreed && condition?.isRequired == true) {
            throw BusinessException(ConsentErrorCode.REQUIRED_CONSENT_CANNOT_BE_WITHDRAWN)
        }
        val now = LocalDateTime.now()
        val consent = userConsentRepository.findByUserIdAndConsentType(userId, request.consentType)
            .orElseThrow { BusinessException(ConsentErrorCode.CONSENT_NOT_FOUND) }
        consent.agreed = request.agreed
        if (request.agreed) {
            consent.agreedAt = now
        }
        userConsentHistoryRepository.save(
            UserConsentHistory(
                userId = userId,
                consentType = request.consentType,
                agreed = request.agreed,
                changedAt = now,
            )
        )
    }

    @Transactional
    override fun withdraw(userId: Long, currentPassword: String?) {
        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }
        if (user.withdrawnAt != null) throw BusinessException(ProfileErrorCode.ALREADY_WITHDRAWN)
        if (user.password != null) {
            if (currentPassword.isNullOrBlank() || !passwordEncoder.matches(currentPassword, user.password)) {
                throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            }
        }
        user.withdrawnAt = LocalDateTime.now(ZoneOffset.UTC)
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                jwtTokenProvider.deleteRefreshToken(userId, "ROLE_USER")
            }
        })
    }
}
