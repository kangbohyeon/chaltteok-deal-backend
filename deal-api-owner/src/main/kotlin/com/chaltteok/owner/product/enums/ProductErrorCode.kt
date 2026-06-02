package com.chaltteok.owner.product.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class ProductErrorCode(
    override val message: String,
    override val status: HttpStatus
): ErrorCode {
    PRODUCT_NOT_FOUND("Product not found", HttpStatus.NOT_FOUND),
    FILE_EMPTY("File is empty", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("This is an unsupported file type", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_ERROR("Error uploading file", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_STOCK("현재 잔고는 일별 재고 수량을 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),
}