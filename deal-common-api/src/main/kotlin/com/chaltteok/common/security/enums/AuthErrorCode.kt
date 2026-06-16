package com.chaltteok.common.security.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "토큰 정보가 일치하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "계정이 잠겼습니다. 관리자에게 문의하세요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 계정을 찾을 수 없습니다."),
}
