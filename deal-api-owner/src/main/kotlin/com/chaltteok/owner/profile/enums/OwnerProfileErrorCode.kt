package com.chaltteok.owner.profile.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class OwnerProfileErrorCode(
    override val status: HttpStatus, override val message: String,
) : ErrorCode {
    ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "이미 탈퇴한 계정입니다."),
}
