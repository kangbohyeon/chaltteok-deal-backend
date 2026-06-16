package com.chaltteok.user.comment.enums

import com.chaltteok.common.enums.ErrorCode
import org.springframework.http.HttpStatus

enum class CommentErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "댓글 수정/삭제 권한이 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
}
