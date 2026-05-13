package com.chaltteok.user.profile.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.profile.dto.UpdateNicknameRequest
import com.chaltteok.user.profile.dto.UserProfileResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProfileServiceImpl(
    private val userRepository: UserRepository,
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
}
