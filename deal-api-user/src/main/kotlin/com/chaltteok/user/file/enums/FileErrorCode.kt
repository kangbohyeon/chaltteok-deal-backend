package com.chaltteok.user.file.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class FileErrorCode(
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    FILE_EMPTY("파일이 비어있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("JPEG, PNG 파일만 첨부 가능합니다.", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
}
