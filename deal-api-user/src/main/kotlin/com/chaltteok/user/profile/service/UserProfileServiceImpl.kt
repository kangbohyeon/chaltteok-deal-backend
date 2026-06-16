package com.chaltteok.user.profile.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.profile.dto.ChangePasswordRequest
import com.chaltteok.user.profile.dto.UpdateNicknameRequest
import com.chaltteok.user.profile.dto.UserProfileResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserProfileServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserProfileService {

    companion object {
        private const val PASSWORD_EXPIRY_DAYS = 90L
    }

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

        // 임시 비밀번호 로그인 또는 비밀번호 만료로 인한 강제 변경 플로우는
        // currentPassword 입력을 받지 않으므로 검증을 건너뜀
        val isForcedChange = user.requirePasswordChange ||
            user.passwordChangedAt == null ||
            user.passwordChangedAt!!.isBefore(LocalDateTime.now().minusDays(PASSWORD_EXPIRY_DAYS))

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
}
