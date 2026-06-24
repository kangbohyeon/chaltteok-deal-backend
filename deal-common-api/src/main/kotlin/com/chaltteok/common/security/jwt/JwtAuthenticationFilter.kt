package com.chaltteok.common.security.jwt

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

// 이 필터는 deal-gateway가 서명한 X-Internal-Sig 헤더를 검증하여 인증 컨텍스트를 설정한다.
// 네트워크 정책(Security Group / K8s NetworkPolicy)으로 Gateway 외부에서 직접 접근을 차단해야 한다.
@Component
class JwtAuthenticationFilter(
    @Value("\${gateway.internal-secret}") private val internalSecret: String,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val userId = request.getHeader("X-User-Id")?.toLongOrNull()
        val role = request.getHeader("X-User-Role")?.takeIf { it.isNotBlank() }
        val sig = request.getHeader("X-Internal-Sig")

        if (userId != null && role != null && sig != null) {
            val expected = computeExpectedSig(userId.toString(), role)
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

    private fun computeExpectedSig(userId: String, role: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(internalSecret.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return Base64.getEncoder().encodeToString(mac.doFinal("$userId:$role".toByteArray(Charsets.UTF_8)))
    }
}
