package com.chaltteok.filter

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import javax.crypto.SecretKey

private val log = KotlinLogging.logger {}
private const val ROLE_CLAIM = "role"

@Component
class JwtAuthGatewayFilter(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${gateway.public-paths}") private val publicPaths: List<String>,
) : GlobalFilter, Ordered {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))
    }
    private val pathMatcher = AntPathMatcher()

    // HttpLoggingFilter(HIGHEST_PRECEDENCE)보다 낮은 순서
    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 1

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val path = request.uri.path
        val method = request.method

        // 헤더 위조 방지: 외부 요청의 X-User-* 헤더 제거
        val sanitized = exchange.mutate()
            .request(request.mutate()
                .headers { h -> h.remove("X-User-Id"); h.remove("X-User-Role") }
                .build())
            .build()

        // OPTIONS(CORS preflight) 및 공개 경로는 인증 없이 통과
        if (method == HttpMethod.OPTIONS || isPublicPath(method, path)) {
            return chain.filter(sanitized)
        }

        val token = resolveToken(request) ?: return unauthorized(exchange)

        return try {
            val claims = parseClaims(token)
            val userId = claims.subject
            val role = claims[ROLE_CLAIM, String::class.java]

            val authed = sanitized.mutate()
                .request(sanitized.request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .build())
                .build()

            log.debug { "JWT 검증 성공 — userId=$userId, role=$role, path=$path" }
            chain.filter(authed)
        } catch (e: JwtException) {
            log.warn { "JWT 검증 실패 — path=$path, message=${e.message}" }
            unauthorized(exchange)
        }
    }

    private fun resolveToken(request: ServerHttpRequest): String? {
        val bearer = request.headers.getFirst("Authorization")
        if (bearer != null && bearer.startsWith("Bearer ")) return bearer.substring(7)
        // SSE: EventSource는 커스텀 헤더 미지원 → 쿼리 파라미터 토큰 허용
        if (request.uri.path.endsWith("/notifications/sse")) {
            return request.queryParams.getFirst("token")
        }
        return null
    }

    private fun isPublicPath(@Suppress("UNUSED_PARAMETER") method: HttpMethod, path: String): Boolean =
        publicPaths.any { pattern -> pathMatcher.match(pattern, path) }

    private fun parseClaims(token: String): Claims =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload

    private fun unauthorized(exchange: ServerWebExchange): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON
        val body = """{"result":"ERROR","errorCode":"A002","errorMessage":"유효하지 않은 토큰입니다."}"""
        val buffer = response.bufferFactory().wrap(body.toByteArray(Charsets.UTF_8))
        return response.writeWith(Mono.just(buffer))
    }
}
