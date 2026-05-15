package com.chaltteok.owner.popup.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class PopupErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    POPUP_NOT_FOUND(HttpStatus.NOT_FOUND, "팝업을 찾을 수 없습니다."),
}
