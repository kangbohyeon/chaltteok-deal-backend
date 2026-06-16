package com.chaltteok.owner.inquiry.dto

import jakarta.validation.constraints.NotBlank

class AnswerRequest(
    @field:NotBlank val answer: String,
)
