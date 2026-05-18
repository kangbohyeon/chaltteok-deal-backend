package com.chaltteok.owner.comment.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class OwnerCommentErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
}
