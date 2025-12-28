package com.chaltteok.common.enums

import org.springframework.http.HttpStatus

enum class GlobalErrorCode(
    override val message: String,
    override val status: HttpStatus
): ErrorCode {
    //server
    INTERNAL_SERVER_ERROR("internal server error has occurred", HttpStatus.INTERNAL_SERVER_ERROR),


    //client
    INVALID_INPUT_VALUE("Invalid input value", HttpStatus.BAD_REQUEST),
    INVALID_TYPE_VALUE("The input value type is not valid", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("Unsupported HTTP method", HttpStatus.METHOD_NOT_ALLOWED),
    FILE_TOO_LARGE("File size cannot exceed", HttpStatus.PAYLOAD_TOO_LARGE),
    URL_NOT_FOUND("The URL you requested could not be found", HttpStatus.NOT_FOUND);
}