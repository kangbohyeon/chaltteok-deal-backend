package com.chaltteok.user.inquiry.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class InquiryRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val content: String,
    @field:Size(max = 3) val attachmentUuids: List<String> = emptyList(),
)
