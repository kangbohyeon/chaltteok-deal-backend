package com.chaltteok.user.inquiry.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class InquiryErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "문의를 찾을 수 없습니다."),
    INQUIRY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인의 문의만 수정/삭제할 수 있습니다."),
    INQUIRY_ALREADY_ANSWERED(HttpStatus.BAD_REQUEST, "답변 완료된 문의는 수정/삭제할 수 없습니다."),
}
