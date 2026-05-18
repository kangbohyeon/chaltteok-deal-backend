package com.chaltteok.user.notice.dto

import com.chaltteok.core.domain.Notice
import java.time.LocalDateTime

class NoticeResponse(
    val noticeUuid: String,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(notice: Notice) = NoticeResponse(
            noticeUuid = notice.noticeUuid,
            title = notice.title,
            content = notice.content,
            createdAt = notice.createdAt,
        )
    }
}
