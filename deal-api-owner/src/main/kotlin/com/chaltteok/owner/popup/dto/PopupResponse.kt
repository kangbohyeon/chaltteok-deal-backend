package com.chaltteok.owner.popup.dto

import com.chaltteok.core.domain.Popup
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PopupResponse(
    val popupUuid: String,
    val title: String,
    val content: String,
    @get:JsonProperty("isVisible") val isVisible: Boolean,
    val location: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(popup: Popup) = PopupResponse(
            popupUuid = popup.popupUuid,
            title = popup.title,
            content = popup.content,
            isVisible = popup.isVisible,
            location = popup.location,
            startDate = popup.startDate,
            endDate = popup.endDate,
            startTime = popup.startTime,
            endTime = popup.endTime,
            createdAt = popup.createdAt,
        )
    }
}
