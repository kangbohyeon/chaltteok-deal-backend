package com.chaltteok.owner.banner.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class BannerErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    BANNER_NOT_FOUND(HttpStatus.NOT_FOUND, "배너를 찾을 수 없습니다."),
}
