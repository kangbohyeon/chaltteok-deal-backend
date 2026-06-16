package com.chaltteok.user.auth.service

import com.chaltteok.core.repository.user.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class LoginFailureRecorder(
    private val userRepository: UserRepository,
) {
    companion object {
        const val MAX_LOGIN_FAIL_COUNT = 5
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun recordFailure(userId: Long) {
        userRepository.incrementFailedCountAndLockIfNeeded(userId, MAX_LOGIN_FAIL_COUNT, LocalDateTime.now())
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun resetFailure(userId: Long) {
        userRepository.resetFailedCount(userId)
    }
}
