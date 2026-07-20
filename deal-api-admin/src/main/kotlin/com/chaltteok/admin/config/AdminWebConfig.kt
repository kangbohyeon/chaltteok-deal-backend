package com.chaltteok.admin.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Order(1)
class AdminWebConfig(
    @Value("\${spring.security.user.name:admin}") private val adminUsername: String,
    @Value("\${spring.security.user.password:{noop}changeme}") private val adminPassword: String,
) {

    @Bean
    fun adminSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        val userDetails = User.withUsername(adminUsername)
            .password(adminPassword)
            .roles("ADMIN")
            .build()
        val userDetailsService: UserDetailsService = InMemoryUserDetailsManager(userDetails)

        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(encoder)

        http
            .securityMatcher("/admin/**")
            .authenticationProvider(authProvider)
            .authorizeHttpRequests { auth -> auth.anyRequest().authenticated() }
            .httpBasic(withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
        return http.build()
    }
}
