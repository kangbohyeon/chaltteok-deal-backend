package com.chaltteok.user.auth.dto

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class RegisterRequest(
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank
    @field:Size(min = 10, max = 255)
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,255}$",
        message = "비밀번호는 10자 이상, 대소문자·숫자·특수문자를 각 1개 이상 포함해야 합니다."
    )
    val password: String,
    @field:NotBlank val name: String,
    @field:NotBlank val phone: String,
    @field:AssertTrue(message = "서비스 이용약관에 동의해야 합니다.") val termsAgreed: Boolean,
    @field:AssertTrue(message = "개인정보 처리방침에 동의해야 합니다.") val privacyAgreed: Boolean,
    @field:AssertTrue(message = "만 14세 이상이어야 가입할 수 있습니다.") val ageAgreed: Boolean,
    val marketingAgreed: Boolean = false,
    val pushAgreed: Boolean = false,
)
