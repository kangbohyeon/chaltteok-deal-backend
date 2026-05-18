package com.chaltteok.owner.popup.dto

import com.chaltteok.core.domain.Notice
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PopupResponse(
    val popupUuid: String,
    val title: String,
    val content: String,
    val isVisible: Boolean,
    val location: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(notice: Notice) = PopupResponse(
            popupUuid = notice.noticeUuid,
            title = notice.title,
            content = notice.content,
            isVisible = notice.isVisible,
            location = notice.location,
            startDate = notice.startDate,
            endDate = notice.endDate,
            startTime = notice.startTime,
            endTime = notice.endTime,
            createdAt = notice.createdAt,
        )
    }
}
