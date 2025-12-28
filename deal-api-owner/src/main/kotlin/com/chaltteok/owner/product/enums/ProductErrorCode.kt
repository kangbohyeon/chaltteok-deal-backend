package com.chaltteok.owner.product.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class ProductErrorCode(
    override val message: String,
    override val status: HttpStatus
): ErrorCode {
    FILE_EMPTY("File is empty", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("This is an unsupported file type", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_ERROR("Error uploading file", HttpStatus.INTERNAL_SERVER_ERROR),

}