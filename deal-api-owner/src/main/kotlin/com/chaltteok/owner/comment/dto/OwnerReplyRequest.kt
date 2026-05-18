package com.chaltteok.owner.comment.dto

import jakarta.validation.constraints.NotBlank

class OwnerReplyRequest(
    @field:NotBlank val content: String,
)
