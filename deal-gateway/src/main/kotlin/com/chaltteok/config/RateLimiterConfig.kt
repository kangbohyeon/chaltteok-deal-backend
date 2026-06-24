package com.chaltteok.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration
class RateLimiterConfig {
    // IP 기반 Rate Limiting — 클라이언트 IP를 키로 사용
    @Bean
    fun ipKeyResolver(): KeyResolver = KeyResolver { exchange ->
        val ip = exchange.request.headers.getFirst("X-Forwarded-For")
            ?.split(",")?.firstOrNull()?.trim()
            ?: exchange.request.remoteAddress?.address?.hostAddress
            ?: "unknown"
        Mono.just(ip)
    }
}
