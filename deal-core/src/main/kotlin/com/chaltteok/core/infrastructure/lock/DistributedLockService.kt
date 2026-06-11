package com.chaltteok.core.infrastructure.lock

import io.github.oshai.kotlinlogging.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Component
class DistributedLockService(private val redissonClient: RedissonClient) {

    fun <T> withLock(
        key: String,
        waitSec: Long = 3,
        leaseSec: Long = 5,
        onFail: () -> T,
        block: () -> T,
    ): T {
        val lock = redissonClient.getLock(key)
        val acquired = lock.tryLock(waitSec, leaseSec, TimeUnit.SECONDS)
        if (!acquired) {
            log.warn { "분산 락 획득 실패 — key=$key" }
            return onFail()
        }
        return try {
            block()
        } finally {
            if (lock.isHeldByCurrentThread) lock.unlock()
        }
    }

    fun <T> withMultiLock(
        keys: List<String>,
        waitSec: Long = 3,
        leaseSec: Long = 5,
        onFail: () -> T,
        block: () -> T,
    ): T {
        val locks = keys.sorted().map { redissonClient.getLock(it) }
        val multiLock = redissonClient.getMultiLock(*locks.toTypedArray())
        val acquired = multiLock.tryLock(waitSec, leaseSec, TimeUnit.SECONDS)
        if (!acquired) {
            log.warn { "분산 멀티 락 획득 실패 — keys=$keys" }
            return onFail()
        }
        return try {
            block()
        } finally {
            if (multiLock.isHeldByCurrentThread) multiLock.unlock()
        }
    }
}
