package com.chaltteok.common.security.jwt

import jakarta.annotation.PostConstruct
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// 이 필터는 deal-gateway가 서명한 X-Gateway-Token 및 X-Internal-Sig 헤더를 검증하여 인증 컨텍스트를 설정한다.
// 네트워크 정책(Security Group / K8s NetworkPolicy)으로 Gateway 외부에서 직접 접근을 차단해야 한다.
@Component
class JwtAuthenticationFilter(
    @Value("\${gateway.internal-secret}") private val internalSecret: String,
) : OncePerRequestFilter() {

    private val gatewayToken: String by lazy {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(internalSecret.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        Base64.getEncoder().encodeToString(mac.doFinal("gateway-request".toByteArray(Charsets.UTF_8)))
    }

    @PostConstruct
    fun validateConfig() {
        require(internalSecret.toByteArray(Charsets.UTF_8).size >= 32) {
            "gateway.internal-secret must be at least 32 bytes (current: ${internalSecret.toByteArray(Charsets.UTF_8).size})"
        }
        gatewayToken
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.requestURI == "/admin" || request.requestURI.startsWith("/admin/")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val gwToken = request.getHeader("X-Gateway-Token")
        if (!MessageDigest.isEqual(
                gatewayToken.toByteArray(Charsets.UTF_8),
                (gwToken ?: "").toByteArray(Charsets.UTF_8)
            )
        ) {
            response.status = 403
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write("""{"result":"ERROR","errorCode":"A005","errorMessage":"접근 권한이 없습니다."}""")
            return
        }

        val userId = request.getHeader("X-User-Id")?.toLongOrNull()
        val role = request.getHeader("X-User-Role")?.takeIf { it.isNotBlank() }
        val sig = request.getHeader("X-Internal-Sig")

        if (userId != null && role != null && sig != null) {
            val expected = computeInternalSig(userId.toString(), role)
            // timing-safe 비교 — sig 길이 차이 포함 상수 시간 비교
            if (MessageDigest.isEqual(expected.toByteArray(Charsets.UTF_8), sig.toByteArray(Charsets.UTF_8))) {
                val auth = UsernamePasswordAuthenticationToken(
                    userId, null, listOf(SimpleGrantedAuthority(role))
                )
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request, response)
    }

    // JwtAuthGatewayFilter.computeInternalSig와 반드시 동일한 알고리즘 사용해야 한다.
    // (HmacSHA256, payload="$userId:$role", Base64 인코딩) — 양측 불일치 시 인증 전면 실패
    private fun computeInternalSig(userId: String, role: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(internalSecret.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return Base64.getEncoder().encodeToString(mac.doFinal("$userId:$role".toByteArray(Charsets.UTF_8)))
    }
}
