package com.chaltteok.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration
class RateLimiterConfig {
    @Bean
    fun ipKeyResolver(): KeyResolver = KeyResolver { exchange ->
        // 실제 TCP 연결 IP 우선 (위조 불가) — X-Forwarded-For는 클라이언트가 위조하여 Rate Limiting을 우회할 수 있어 신뢰하지 않는다.
        // K8s 환경에서 externalTrafficPolicy: Local 설정 시 remoteAddress에 실 클라이언트 IP가 보존된다.
        val ip = exchange.request.remoteAddress?.address?.hostAddress
            ?: exchange.request.headers.getFirst("X-Real-IP")
            ?: "unknown"
        Mono.just(ip)
    }
}
