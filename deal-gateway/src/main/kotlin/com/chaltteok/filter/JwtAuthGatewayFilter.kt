package com.chaltteok.filter

import com.chaltteok.config.GatewayProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
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
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

private val log = KotlinLogging.logger {}
private const val ROLE_CLAIM = "role"
private val UNAUTHORIZED_BODY =
    """{"result":"ERROR","errorCode":"A002","errorMessage":"유효하지 않은 토큰입니다."}"""
        .toByteArray(Charsets.UTF_8)

@Component
class JwtAuthGatewayFilter(
    @Value("\${jwt.secret}") private val secret: String,
    private val gatewayProps: GatewayProperties,
) : GlobalFilter, Ordered {

    private val key: SecretKey by lazy {
        val bytes = secret.toByteArray(Charsets.UTF_8)
        require(bytes.size >= 32) { "jwt.secret must be at least 32 bytes for HMAC-SHA256 (current: ${bytes.size})" }
        Keys.hmacShaKeyFor(bytes)
    }
    private val jwtParser: JwtParser by lazy {
        Jwts.parser().verifyWith(key).build()
    }
    private val pathMatcher = AntPathMatcher()

    @PostConstruct
    fun validateConfig() {
        // 애플리케이션 시작 시점에 JWT 키 길이 검증 및 JwtParser 사전 초기화 — 배포 후 런타임 예외 방지
        key
        jwtParser
    }

    // 헤더 sanitize + JWT 검증은 로깅 필터(HIGHEST_PRECEDENCE) 다음으로 실행
    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 1

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val path = request.uri.path
        val method = request.method

        // 외부 요청에서 X-User-* / X-Internal-Sig 위조 방지 — 헤더가 존재할 때만 mutate
        val hasInjectedHeaders = request.headers.containsKey("X-User-Id")
            || request.headers.containsKey("X-User-Role")
            || request.headers.containsKey("X-Internal-Sig")
        val sanitized = if (hasInjectedHeaders) {
            exchange.mutate()
                .request(request.mutate()
                    .headers { h ->
                        h.remove("X-User-Id")
                        h.remove("X-User-Role")
                        h.remove("X-Internal-Sig")
                    }
                    .build())
                .build()
        } else exchange

        // OPTIONS(CORS preflight) 및 공개 경로는 인증 없이 통과
        if (method == HttpMethod.OPTIONS || isPublicPath(path)) {
            return chain.filter(sanitized)
        }

        val token = resolveToken(sanitized.request) ?: return unauthorized(exchange)

        return try {
            val claims = parseClaims(token)
            val userId = claims.subject
            val role = claims[ROLE_CLAIM, String::class.java]
            // Gateway가 서명한 내부 신뢰 토큰 — 하위 서비스에서 헤더 위조 여부 검증에 사용
            val internalSig = computeInternalSig(userId, role)

            val authed = sanitized.mutate()
                .request(sanitized.request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-Internal-Sig", internalSig)
                    .build())
                .build()

            log.debug { "JWT 검증 성공 — path=$path" }
            chain.filter(authed)
        } catch (e: JwtException) {
            log.warn { "JWT 검증 실패 — path=$path, message=${e.message}" }
            unauthorized(exchange)
        }
    }

    private fun resolveToken(request: ServerHttpRequest): String? {
        val bearer = request.headers.getFirst("Authorization")
        if (bearer != null && bearer.startsWith("Bearer ")) return bearer.substring(7)
        // SSE: EventSource는 커스텀 헤더 미지원 → gateway.sse-paths에 설정된 경로에서만 쿼리 파라미터 허용
        if (gatewayProps.ssePaths.any { pathMatcher.match(it, request.uri.path) }) {
            return request.queryParams.getFirst("token")
        }
        return null
    }

    private fun isPublicPath(path: String): Boolean =
        gatewayProps.publicPaths.any { pattern -> pathMatcher.match(pattern, path) }

    private fun parseClaims(token: String): Claims =
        jwtParser.parseSignedClaims(token).payload

    // Gateway가 서명한 내부 신뢰 토큰 — JwtAuthenticationFilter.computeInternalSig와 반드시 동일한 알고리즘 사용해야 한다.
    // (HmacSHA256, payload="$userId:$role", Base64 인코딩) — 양측 불일치 시 인증 전면 실패
    private fun computeInternalSig(userId: String, role: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(gatewayProps.internalSecret.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return Base64.getEncoder().encodeToString(mac.doFinal("$userId:$role".toByteArray(Charsets.UTF_8)))
    }

    private fun unauthorized(exchange: ServerWebExchange): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON
        val buffer = response.bufferFactory().wrap(UNAUTHORIZED_BODY)
        return response.writeWith(Mono.just(buffer))
    }
}
