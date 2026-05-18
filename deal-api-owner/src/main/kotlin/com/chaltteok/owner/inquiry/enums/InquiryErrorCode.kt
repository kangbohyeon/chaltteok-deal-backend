package com.chaltteok.owner.inquiry.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class InquiryErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "문의를 찾을 수 없습니다."),
}
