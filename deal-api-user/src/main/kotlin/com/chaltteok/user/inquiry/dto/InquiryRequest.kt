package com.chaltteok.user.inquiry.dto

import jakarta.validation.constraints.NotBlank

class InquiryRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val content: String,
)
