package com.chaltteok.owner.notice.dto

import jakarta.validation.constraints.NotBlank

class NoticeRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val content: String,
    val isVisible: Boolean = true,
)
