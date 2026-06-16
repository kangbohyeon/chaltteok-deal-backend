package com.chaltteok.user.popup.dto

import com.chaltteok.core.domain.Popup
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
        fun from(popup: Popup) = PopupResponse(
            popupUuid = popup.popupUuid,
            title = popup.title,
            content = popup.content,
            location = popup.location,
            startDate = popup.startDate,
            endDate = popup.endDate,
        )
    }
}
