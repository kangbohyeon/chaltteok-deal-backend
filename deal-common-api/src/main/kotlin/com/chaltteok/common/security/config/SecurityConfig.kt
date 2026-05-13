package com.chaltteok.common.security.config

import com.chaltteok.common.security.jwt.JwtAuthenticationFilter
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors(withDefaults())
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // owner
                    .requestMatchers("/api/v1/owner/auth/**").permitAll()
                    .requestMatchers("/api/v1/owner/**").hasAuthority("ROLE_OWNER")
                    // user — 로그인 없이 접근 가능한 공개 엔드포인트
                    .requestMatchers("/api/v1/user/auth/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/user/daily-stocks/open").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/user/products/**").permitAll()
                    // 정적 이미지 파일 공개 접근
                    .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint { _, response, _ ->
                    response.status = 401
                    response.contentType = "application/json;charset=UTF-8"
                    response.writer.write("""{"result":"ERROR","errorCode":"A002","errorMessage":"유효하지 않은 토큰입니다."}""")
                }
                ex.accessDeniedHandler { _, response, _ ->
                    response.status = 403
                    response.contentType = "application/json;charset=UTF-8"
                    response.writer.write("""{"result":"ERROR","errorCode":"A005","errorMessage":"접근 권한이 없습니다."}""")
                }
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
