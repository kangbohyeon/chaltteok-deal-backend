package com.chaltteok.common.enums

import com.chaltteok.common.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class GlobalErrorCode(
    override val message: String,
    override val status: HttpStatus
):ErrorCode {
    INTERNAL_SERVER_ERROR("internal server error has occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT_VALUE("Invalid input value", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("Unsupported HTTP method", HttpStatus.METHOD_NOT_ALLOWED);
}