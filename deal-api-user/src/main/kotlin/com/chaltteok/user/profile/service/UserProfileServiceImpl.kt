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
}
