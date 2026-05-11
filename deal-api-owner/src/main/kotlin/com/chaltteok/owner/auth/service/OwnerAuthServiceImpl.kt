package com.chaltteok.owner.auth.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.TokenDto
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.core.repository.owner.OwnerRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
    override fun login(username: String, password: String): TokenDto {
        val owner = ownerRepository.findByUsername(username)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }

        if (!passwordEncoder.matches(password, owner.password)) {
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        val userId = owner.id ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        val accessToken = jwtTokenProvider.generateAccessToken(userId, ROLE)
        val refreshToken = jwtTokenProvider.generateRefreshToken(userId, ROLE)
        return TokenDto(accessToken, refreshToken, userId)
    }
}
