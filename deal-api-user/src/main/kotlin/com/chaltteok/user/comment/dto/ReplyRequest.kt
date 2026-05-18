package com.chaltteok.user.comment.dto

import jakarta.validation.constraints.NotBlank

class ReplyRequest(
    @field:NotBlank val content: String,
)
