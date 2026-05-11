package com.chaltteok.user.order.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class OrderErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DAILY_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "일일 재고를 찾을 수 없습니다."),
    STOCK_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "주문 가능한 상태가 아닙니다."),
    ALREADY_PARTICIPATED(HttpStatus.CONFLICT, "이미 참여한 이벤트입니다."),
}
