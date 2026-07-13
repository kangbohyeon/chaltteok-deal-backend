package com.chaltteok.owner.profile.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.core.repository.owner.OwnerRepository
import com.chaltteok.owner.profile.dto.ChangeOwnerPasswordRequest
import com.chaltteok.owner.profile.dto.OwnerProfileResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OwnerProfileServiceImpl(
    private val ownerRepository: OwnerRepository,
    private val passwordEncoder: PasswordEncoder,
) : OwnerProfileService {

    @Transactional(readOnly = true)
    override fun getProfile(ownerId: Long): OwnerProfileResponse {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { BusinessException(AuthErrorCode.USER_NOT_FOUND) }
        return OwnerProfileResponse.from(owner)
    }

    @Transactional
    override fun changePassword(ownerId: Long, request: ChangeOwnerPasswordRequest) {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }

        if (!request.currentPassword.isNullOrBlank() &&
            !passwordEncoder.matches(request.currentPassword, owner.password)) {
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        owner.password = passwordEncoder.encode(request.newPassword)
        owner.passwordChangedAt = LocalDateTime.now()
    }
}
