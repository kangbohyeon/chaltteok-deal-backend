package com.chaltteok.owner.dailystock.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class DailyStockErrorCode (
    override val message: String,
    override val status: HttpStatus
):ErrorCode{
    INVALID_ID("Invalid id",HttpStatus.BAD_REQUEST),
    EVENT_PRICE_REQUIRED("I don't have a price",HttpStatus.BAD_REQUEST),
    DUPLICATE_STOCK("It's already registered in stock",HttpStatus.BAD_REQUEST),
}