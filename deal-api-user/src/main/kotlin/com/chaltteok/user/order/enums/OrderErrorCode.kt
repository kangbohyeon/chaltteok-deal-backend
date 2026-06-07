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
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),
    ALREADY_PARTICIPATED(HttpStatus.CONFLICT, "이미 참여한 이벤트입니다."),
    EXCEEDS_MAX_PURCHASE_COUNT(HttpStatus.BAD_REQUEST, "1인 최대 구매 수량을 초과했습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "이미 취소된 주문입니다."),
    ORDER_NOT_CANCELLABLE(HttpStatus.BAD_REQUEST, "취소할 수 없는 주문 상태입니다."),
}
