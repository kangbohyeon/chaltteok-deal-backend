package com.chaltteok.owner.profile.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.core.repository.owner.OwnerRepository
import com.chaltteok.owner.profile.dto.ChangeOwnerPasswordRequest
import com.chaltteok.owner.profile.dto.OwnerProfileResponse
import com.chaltteok.owner.profile.enums.OwnerProfileErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class OwnerProfileServiceImpl(
    private val ownerRepository: OwnerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
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

    @Transactional
    override fun withdraw(ownerId: Long, currentPassword: String) {
        val owner = ownerRepository.findById(ownerId)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }
        if (owner.withdrawnAt != null) throw BusinessException(OwnerProfileErrorCode.ALREADY_WITHDRAWN)
        if (!passwordEncoder.matches(currentPassword, owner.password)) {
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }
        owner.withdrawnAt = LocalDateTime.now(ZoneOffset.UTC)
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                jwtTokenProvider.deleteRefreshToken(ownerId, "ROLE_OWNER")
            }
        })
    }
}
