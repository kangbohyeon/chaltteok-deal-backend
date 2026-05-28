package com.chaltteok.owner.auth.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.LoginResponseDto
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.core.repository.owner.OwnerRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class OwnerAuthServiceImpl(
    private val ownerRepository: OwnerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) : OwnerAuthService {

    companion object {
        private const val ROLE = "ROLE_OWNER"
    }

    @Transactional(readOnly = true)
    override fun login(username: String, password: String): LoginResponseDto {
        val owner = ownerRepository.findByUsername(username)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }

        if (!passwordEncoder.matches(password, owner.password)) {
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        val ownerId = owner.id ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)

        val requirePasswordChange = owner.passwordChangedAt == null ||
            owner.passwordChangedAt!!.isBefore(LocalDateTime.now().minusDays(90))

        val accessToken = jwtTokenProvider.generateAccessToken(ownerId, ROLE)
        val refreshToken = jwtTokenProvider.generateRefreshToken(ownerId, ROLE)
        return LoginResponseDto(accessToken, refreshToken, owner.ownerUuid, requirePasswordChange)
    }
}
