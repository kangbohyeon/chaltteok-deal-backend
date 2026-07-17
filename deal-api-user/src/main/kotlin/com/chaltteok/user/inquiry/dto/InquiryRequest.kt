package com.chaltteok.user.inquiry.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class InquiryRequest(
    @field:NotBlank @field:Size(max = 200) val title: String,
    @field:NotBlank @field:Size(max = 5000) val content: String,
    @field:Size(max = 3) val attachmentUuids: List<String> = emptyList(),
)
