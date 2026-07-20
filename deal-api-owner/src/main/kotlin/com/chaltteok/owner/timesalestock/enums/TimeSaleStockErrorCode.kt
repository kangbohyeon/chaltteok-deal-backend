package com.chaltteok.owner.timesalestock.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class TimeSaleStockErrorCode(
    override val message: String,
    override val status: HttpStatus
) : ErrorCode {
    INVALID_ID("Invalid stock id", HttpStatus.BAD_REQUEST),
    INVALID_OPTION_ID("Invalid option id", HttpStatus.BAD_REQUEST),
    EVENT_PRICE_REQUIRED("I don't have a price", HttpStatus.BAD_REQUEST),
    DUPLICATE_STOCK("It's already registered in stock", HttpStatus.BAD_REQUEST),
}
