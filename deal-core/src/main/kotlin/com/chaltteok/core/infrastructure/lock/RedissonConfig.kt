package com.chaltteok.core.infrastructure.lock

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig(
    @Value("\${spring.data.redis.host}") private val host: String,
    @Value("\${spring.data.redis.port}") private val port: Int,
    @Value("\${spring.data.redis.password:#{null}}") private val password: String?,
    @Value("\${spring.data.redis.ssl.enabled:false}") private val sslEnabled: Boolean,
) {
    @Bean
    fun redissonClient(): RedissonClient {
        val scheme = if (sslEnabled) "rediss" else "redis"
        val config = Config()
        config.useSingleServer()
            .setAddress("$scheme://$host:$port")
            .setConnectionPoolSize(10)
            .setConnectionMinimumIdleSize(2)
            .also { if (!password.isNullOrBlank()) it.setPassword(password) }
        return Redisson.create(config)
    }
}
