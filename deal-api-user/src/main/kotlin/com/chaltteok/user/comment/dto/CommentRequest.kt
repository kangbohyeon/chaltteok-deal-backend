package com.chaltteok.user.comment.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class CommentRequest(
    @field:NotBlank val content: String,
    @field:Min(1) @field:Max(5) val rating: Int? = null,
    val isSecret: Boolean = false,
    @field:Size(max = 3) val attachmentUuids: List<String> = emptyList(),
)
