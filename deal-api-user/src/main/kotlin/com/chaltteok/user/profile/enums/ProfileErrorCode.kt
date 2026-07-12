package com.chaltteok.user.profile.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class ProfileErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "이미 탈퇴한 계정입니다."),
    WITHDRAWN_ACCOUNT(HttpStatus.FORBIDDEN, "탈퇴한 계정은 로그인할 수 없습니다."),
}
