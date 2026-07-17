package com.chaltteok.owner.comment.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class OwnerReplyRequest(
    @field:NotBlank @field:Size(max = 2000) val content: String,
)
