package com.chaltteok.user.popup.dto

import com.chaltteok.core.domain.Notice
import java.time.LocalDate

class PopupResponse(
    val popupUuid: String,
    val title: String,
    val content: String,
    val location: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
) {
    companion object {
        fun from(notice: Notice) = PopupResponse(
            popupUuid = notice.noticeUuid,
            title = notice.title,
            content = notice.content,
            location = notice.location,
            startDate = notice.startDate,
            endDate = notice.endDate,
        )
    }
}
