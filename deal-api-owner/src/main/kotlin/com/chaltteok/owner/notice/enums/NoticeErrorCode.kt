package com.chaltteok.owner.notice.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class NoticeErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다."),
}
