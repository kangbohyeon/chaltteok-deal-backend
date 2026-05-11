package com.chaltteok.user.auth.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.dto.TokenDto
import com.chaltteok.common.security.enums.AuthErrorCode
import com.chaltteok.common.security.jwt.JwtTokenProvider
import com.chaltteok.core.domain.User
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.auth.dto.RegisterRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAuthServiceImpl(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) : UserAuthService {

    companion object {
        private const val ROLE = "ROLE_USER"
        private const val LOCAL_PROVIDER = "LOCAL"
    }

    @Transactional(readOnly = true)
    override fun login(email: String, password: String): TokenDto {
        val user = userRepository.findByEmail(email)
            .orElseThrow { BusinessException(AuthErrorCode.INVALID_CREDENTIALS) }

        val accessToken = jwtTokenProvider.generateAccessToken(user.id!!, ROLE)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id!!, ROLE)
        return TokenDto(accessToken, refreshToken, user.id!!)
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
        userRepository.save(user)
    }
}
