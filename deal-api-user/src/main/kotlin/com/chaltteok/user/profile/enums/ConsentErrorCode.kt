package com.chaltteok.user.profile.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class ConsentErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    REQUIRED_CONSENT_CANNOT_BE_WITHDRAWN(HttpStatus.BAD_REQUEST, "필수 동의 항목은 철회할 수 없습니다."),
    CONSENT_NOT_FOUND(HttpStatus.NOT_FOUND, "동의 내역을 찾을 수 없습니다."),
}
