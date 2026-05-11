package com.chaltteok.common.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secret: String,
    private val redisTemplate: StringRedisTemplate,
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))
    }

    companion object {
        private const val ACCESS_TOKEN_VALIDITY_MS = 30 * 60 * 1000L       // 30분
        private const val REFRESH_TOKEN_VALIDITY_MS = 7 * 24 * 60 * 60 * 1000L // 7일
        private const val ROLE_CLAIM = "role"
    }

    fun generateAccessToken(userId: Long, role: String): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .claim(ROLE_CLAIM, role)
            .issuedAt(now)
            .expiration(Date(now.time + ACCESS_TOKEN_VALIDITY_MS))
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(userId: Long, role: String): String {
        val now = Date()
        val token = Jwts.builder()
            .subject(userId.toString())
            .claim(ROLE_CLAIM, role)
            .issuedAt(now)
            .expiration(Date(now.time + REFRESH_TOKEN_VALIDITY_MS))
            .signWith(key)
            .compact()
        redisTemplate.opsForValue().set(
            refreshKey(userId, role), token,
            REFRESH_TOKEN_VALIDITY_MS, TimeUnit.MILLISECONDS
        )
        return token
    }

    fun validateToken(token: String): Boolean = runCatching { parseClaims(token) }.isSuccess

    fun getIdFromToken(token: String): Long = parseClaims(token).subject.toLong()

    fun getRoleFromToken(token: String): String = parseClaims(token)[ROLE_CLAIM, String::class.java]

    fun getStoredRefreshToken(userId: Long, role: String): String? =
        redisTemplate.opsForValue().get(refreshKey(userId, role))

    private fun refreshKey(userId: Long, role: String) = "refresh:$role:$userId"

    private fun parseClaims(token: String): Claims =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
}
