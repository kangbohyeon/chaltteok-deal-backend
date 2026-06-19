package com.chaltteok.common.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveToken(request)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            val userId = jwtTokenProvider.getIdFromToken(token)
            val role = jwtTokenProvider.getRoleFromToken(token)
            val auth = UsernamePasswordAuthenticationToken(
                userId, null, listOf(SimpleGrantedAuthority(role))
            )
            SecurityContextHolder.getContext().authentication = auth
            request.setAttribute("X-User-Id", userId)
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearer = request.getHeader("Authorization")
        if (bearer != null && bearer.startsWith("Bearer ")) return bearer.substring(7)
        // EventSource는 커스텀 헤더를 지원하지 않으므로 SSE 엔드포인트에 한해 쿼리 파라미터 토큰 허용
        if (request.requestURI.endsWith("/notifications/sse")) return request.getParameter("token")
        return null
    }
}
