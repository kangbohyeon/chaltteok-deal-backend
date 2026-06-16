package com.chaltteok.user.auth.ratelimit

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.common.security.enums.AuthErrorCode
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class AccountRecoveryRateLimiter(
    private val redisTemplate: StringRedisTemplate,
) {
    companion object {
        private const val MAX_ATTEMPTS = 5
        private val WINDOW = Duration.ofMinutes(10)
        private const val KEY_PREFIX = "account-recovery:"
    }

    fun checkAndIncrement(identifier: String) {
        val key = KEY_PREFIX + identifier
        val count = redisTemplate.opsForValue().increment(key) ?: 1
        if (count == 1L) {
            redisTemplate.expire(key, WINDOW)
        }
        if (count > MAX_ATTEMPTS) {
            throw BusinessException(AuthErrorCode.TOO_MANY_REQUESTS)
        }
    }
}
