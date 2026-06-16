package com.chaltteok.owner.notice.dto

import com.chaltteok.core.domain.Notice
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class NoticeResponse(
    val noticeUuid: String,
    val title: String,
    val content: String,
    @get:JsonProperty("isVisible") val isVisible: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(notice: Notice) = NoticeResponse(
            noticeUuid = notice.noticeUuid,
            title = notice.title,
            content = notice.content,
            isVisible = notice.isVisible,
            createdAt = notice.createdAt,
        )
    }
}
